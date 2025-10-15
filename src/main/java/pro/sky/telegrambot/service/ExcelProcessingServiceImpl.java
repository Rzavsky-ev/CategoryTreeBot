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
 * Реализация сервиса для работы с Excel-файлами категорий.
 */
@Service
public class ExcelProcessingServiceImpl implements ExcelProcessingService {

    private final CategoryRepository categoryRepository;

    public ExcelProcessingServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Генерирует Excel-файл со всеми категориями из базы данных.
     *
     * @return массив байтов с содержимым Excel-файла
     * @throws IOException                  если произошла ошибка ввода-вывода при создании файла
     * @throws CategoryTreeIsEmptyException если в базе данных нет категорий
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
     * Создает стиль для заголовков таблицы.
     *
     * @param workbook книга Excel
     * @return созданный стиль ячеек
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    /**
     * Создает строку заголовков в таблице.
     *
     * @param sheet       лист Excel
     * @param headerStyle стиль для заголовков
     */
    private void createHeaders(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"id_Категории", "Имя_Категории", "id_Родителя"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Заполняет лист данными категорий.
     *
     * @param sheet лист для заполнения
     * @throws CategoryTreeIsEmptyException если в базе нет категорий
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
     * Автоматически настраивает ширину столбцов.
     *
     * @param sheet лист для настройки
     */
    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Парсит Excel-файл и извлекает из него список категорий.
     *
     * @param fileContent содержимое Excel-файла в виде массива байтов
     * @return список распарсенных категорий с восстановленными связями
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
            // Установка родительских связей
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
     * Парсит строку Excel-файла в объект Category.
     *
     * @param row строка Excel-файла
     * @return объект Category
     * @throws InvalidExcelFormatException если данные строки невалидны
     */
    private Category parseCategoryRow(Row row) {
        try {
            Category category = new Category();

            // Обработка ID категории (число)
            Cell idCell = row.getCell(0);
            if (idCell == null || idCell.getCellType() != CellType.NUMERIC) {
                throw new InvalidExcelFormatException("Неверный ID категории в строке " + (row.getRowNum() + 1));
            }
            category.setId((long) idCell.getNumericCellValue());

            // Обработка названия (строка)
            Cell nameCell = row.getCell(1);
            if (nameCell == null || nameCell.getCellType() != CellType.STRING || nameCell.getStringCellValue().isEmpty()) {
                throw new InvalidExcelFormatException("Неверное название в строке " + (row.getRowNum() + 1));
            }
            category.setName(nameCell.getStringCellValue().trim());

            // Обработка родительского ID (число или строка)
            Cell parentCell = row.getCell(2);
            if (parentCell != null) {
                switch (parentCell.getCellType()) {
                    case NUMERIC:  // Числовой формат
                        long parentId = (long) parentCell.getNumericCellValue();
                        if (parentId != 0) {
                            Category parent = new Category();
                            parent.setId(parentId);
                            category.setParent(parent);
                        }
                        break;
                    case STRING:  // Строковый формат
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
                                throw new InvalidExcelFormatException("ID родителя должно быть числом в строке " + (row.getRowNum() + 1));
                            }
                        }
                        break;
                    case BLANK:  // Пустые ячейки
                    case _NONE:
                        break;
                    default:
                        throw new InvalidExcelFormatException("Неверный формат родителя в строке " + (row.getRowNum() + 1));
                }
            }
            return category;
        } catch (Exception e) {
            throw new InvalidExcelFormatException("Ошибка в строке " + (row.getRowNum() + 1) + ": " + e.getMessage());
        }
    }
}
