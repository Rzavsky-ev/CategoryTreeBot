package pro.sky.telegrambot.service;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.exception.CategoryTreeIsEmptyException;
import pro.sky.telegrambot.exception.InvalidExcelFormatException;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.CategoryRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-тесты для {@link ExcelProcessingServiceImpl}, проверяющие функциональность
 * генерации и парсинга Excel-файлов для категорий.
 * <p>
 * Тесты используют Mockito для мокирования зависимостей и проверяют:
 * <ul>
 *   <li>Корректность генерации Excel-файла для категорий</li>
 *   <li>Обработку случаев с пустым деревом категорий</li>
 *   <li>Парсинг корректно сформированных Excel-файлов</li>
 *   <li>Обработку файлов с некорректным форматом</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class ExcelProcessingServiceTest {

    @Mock
    private CategoryRepository categoryRepositoryMock;

    @InjectMocks
    private ExcelProcessingServiceImpl excelProcessingServiceTest;

    private List<Category> testCategories;

    @BeforeEach
    void setUp() {
        testCategories = new ArrayList<>();
        Category parent = new Category("Parent");
        Category child1 = new Category("Child 1");
        Category child2 = new Category("Child 2");

        parent.setId(1L);
        child1.setId(2L);
        child2.setId(3L);

        testCategories.add(parent);
        testCategories.add(child1);
        testCategories.add(child2);

        child1.setParent(parent);
        child2.setParent(parent);

        parent.getChildren().add(child1);
        parent.getChildren().add(child2);

    }


    @Test
    void generateExcelCategoriesTreeNotEmpty() throws IOException {

        when(categoryRepositoryMock.findAll()).thenReturn(testCategories);

        byte[] result = excelProcessingServiceTest.generateCategoriesExcel();

        assertNotNull(result);
        assertTrue(result.length > 0);


        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("Категории", sheet.getSheetName());

            Row headerRow = sheet.getRow(0);
            assertEquals("id", headerRow.getCell(0).getStringCellValue());
            assertEquals("name", headerRow.getCell(1).getStringCellValue());
            assertEquals("parent_id", headerRow.getCell(2).getStringCellValue());

            assertEquals(4, sheet.getPhysicalNumberOfRows());
            assertEquals(1L, sheet.getRow(1).getCell(0).getNumericCellValue());
            assertEquals("Parent", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals("", sheet.getRow(1).getCell(2).getStringCellValue());

            assertEquals(2L, sheet.getRow(2).getCell(0).getNumericCellValue());
            assertEquals("Child 1", sheet.getRow(2).getCell(1).getStringCellValue());
            assertEquals("1", sheet.getRow(2).getCell(2).getStringCellValue());
        }
    }

    @Test
    void generateExcelCategoriesTreeEmpty() {
        String messageException = "Дерево категорий пусто.";

        when(categoryRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(CategoryTreeIsEmptyException.class,
                () -> excelProcessingServiceTest.generateCategoriesExcel());

        verify(categoryRepositoryMock).findAll();
        assertEquals(messageException, exception.getMessage());
    }

    @Test
    void parseExcelCorrectDocument() throws IOException {

        byte[] excelContent = createTestExcelFile();

        List<Category> result = excelProcessingServiceTest.parseExcel(excelContent);

        assertEquals(3, result.size());

        Category parent = result.get(0);
        assertEquals(1L, parent.getId());
        assertEquals("Parent", parent.getName());
        assertNull(parent.getParent());

        Category child1 = result.get(1);
        assertEquals(2L, child1.getId());
        assertEquals("Child 1", child1.getName());
        assertEquals(1L, child1.getParent().getId());

        Category child2 = result.get(2);
        assertEquals(3L, child2.getId());
        assertEquals("Child 2", child2.getName());
        assertEquals(1L, child2.getParent().getId());
    }

    @Test
    void parseExcelNoCorrectDocument() throws IOException {

        byte[] excelContent = createInvalidExcelFileWithWrongId();

        assertThrows(InvalidExcelFormatException.class,
                () -> excelProcessingServiceTest.parseExcel(excelContent));
    }

    private byte[] createTestExcelFile() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet();

            // Header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("name");
            headerRow.createCell(2).setCellValue("parent_id");

            // Data
            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue(1);
            row1.createCell(1).setCellValue("Parent");
            row1.createCell(2).setCellValue("");

            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue(2);
            row2.createCell(1).setCellValue("Child 1");
            row2.createCell(2).setCellValue(1);

            Row row3 = sheet.createRow(3);
            row3.createCell(0).setCellValue(3);
            row3.createCell(1).setCellValue("Child 2");
            row3.createCell(2).setCellValue(1);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private byte[] createInvalidExcelFileWithWrongId() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet();

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("name");
            headerRow.createCell(2).setCellValue("parent_id");

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("invalid_id");
            row1.createCell(1).setCellValue("Test");

            workbook.write(out);
            return out.toByteArray();
        }
    }
}





