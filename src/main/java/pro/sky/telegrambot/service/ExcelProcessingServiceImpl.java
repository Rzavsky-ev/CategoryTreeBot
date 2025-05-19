package pro.sky.telegrambot.service;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.exception.CategoryTreeIsEmptyException;
import pro.sky.telegrambot.exception.InvalidExcelFormatException;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.CategoryRepository;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Сервис для работы с Excel-файлами, содержащими данные о категориях.
 * Предоставляет функциональность для генерации Excel-файла с деревом категорий
 * и парсинга Excel-файла для получения списка категорий.
 */
@Service
public class ExcelProcessingServiceImpl implements ExcelProcessingService {

    private final CategoryRepository categoryRepository;

    public ExcelProcessingServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Генерирует Excel-файл со списком всех категорий из базы данных.
     *
     * @return массив байтов с содержимым Excel-файла
     * @throws IOException                  если произошла ошибка ввода-вывода при работе с файлом
     * @throws CategoryTreeIsEmptyException если в базе данных отсутствуют категории
     */
    @Transactional(readOnly = true)
    @Override
    public byte[] generateCategoriesExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Категории");

            CellStyle headerStyle = createHeaderStyle(workbook);

            createHeaders(sheet, headerStyle);

            processCategoryData(sheet);

            autoSizeColumns(sheet);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Создает стиль для заголовков таблицы в Excel-файле.
     *
     * @param workbook книга Excel, для которой создается стиль
     * @return созданный стиль ячейки
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    /**
     * Создает строку заголовков в листе Excel.
     *
     * @param sheet       лист Excel
     * @param headerStyle стиль для заголовков
     */
    private void createHeaders(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"id", "name", "parent_id"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Обрабатывает данные категорий и заполняет ими лист Excel.
     *
     * @param sheet лист Excel для заполнения данными
     * @throws CategoryTreeIsEmptyException если в базе данных отсутствуют категории
     */
    private void processCategoryData(Sheet sheet) {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new CategoryTreeIsEmptyException("Дерево категорий пусто.");
        }
        int rowNum = 1;

        for (Category category : categories) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(category.getId());
            row.createCell(1).setCellValue(category.getName());

            Long parentId = category.getParent() != null ?
                    category.getParent().getId() : null;
            row.createCell(2).setCellValue(
                    parentId != null ? parentId.toString() : "");
        }
    }

    /**
     * Автоматически изменяет ширину столбцов в листе Excel.
     *
     * @param sheet лист Excel для настройки ширины столбцов
     */
    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Парсит Excel-файл и возвращает список категорий.
     *
     * @param fileContent массив байтов содержимого Excel-файла
     * @return список категорий, полученных из файла
     * @throws IOException                 если произошла ошибка ввода-вывода при чтении файла
     * @throws InvalidExcelFormatException если формат файла не соответствует ожидаемому
     */
    public List<Category> parseExcel(byte[] fileContent) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(fileContent))) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Category> categories = new ArrayList<>();
            Map<Long, Category> categoryMap = new HashMap<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Category category = parseCategoryRow(row);
                categoryMap.put(category.getId(), category);
                categories.add(category);
            }

            for (Category category : categories) {
                if (category.getParent() != null) {
                    Category parent = categoryMap.get(category.getParent().getId());
                    if (parent != null) {
                        category.setParent(parent);
                    }
                }
            }
            return categories;
        }
    }

    /**
     * Парсит строку из Excel-файла в объект Category.
     *
     * @param row строка Excel-файла для парсинга
     * @return объект Category, созданный из данных строки
     * @throws InvalidExcelFormatException если данные в строке имеют неверный формат
     */
    private Category parseCategoryRow(Row row) {
        try {
            Category category = new Category();

            Cell idCell = row.getCell(0);
            if (idCell == null || idCell.getCellType() != CellType.NUMERIC) {
                throw new InvalidExcelFormatException("Отсутствует или неверный формат id в строке " + (row.getRowNum() + 1));
            }
            category.setId((long) idCell.getNumericCellValue());

            Cell nameCell = row.getCell(1);
            if (nameCell == null || nameCell.getCellType() != CellType.STRING || nameCell.getStringCellValue().isEmpty()) {
                throw new InvalidExcelFormatException("Отсутствует или неверный формат имени в строке " + (row.getRowNum() + 1));
            }
            category.setName(nameCell.getStringCellValue().trim());

            Cell parentCell = row.getCell(2);
            if (parentCell != null) {
                switch (parentCell.getCellType()) {
                    case NUMERIC:
                        long parentId = (long) parentCell.getNumericCellValue();
                        if (parentId != 0) {
                            Category parent = new Category();
                            parent.setId(parentId);
                            category.setParent(parent);
                        }
                        break;
                    case STRING:
                        String parentIdStr = parentCell.getStringCellValue().trim();
                        if (!parentIdStr.isEmpty()) {
                            try {
                                parentId = Long.parseLong(parentIdStr);
                                if (parentId != 0) {
                                    Category parent = new Category();
                                    parent.setId(parentId);
                                    category.setParent(parent);
                                }
                            } catch (NumberFormatException e) {
                                throw new InvalidExcelFormatException("Неверный формат parent_id (должно быть число) в строке " + (row.getRowNum() + 1));
                            }
                        }
                        break;
                    case BLANK:
                    case _NONE:
                        break;
                    default:
                        throw new InvalidExcelFormatException("Неверный формат parent_id в строке " + (row.getRowNum() + 1));
                }
            }
            return category;
        } catch (Exception e) {
            throw new InvalidExcelFormatException("Некорректные данные в строке " + (row.getRowNum() + 1) + ": " + e.getMessage());
        }
    }
}




