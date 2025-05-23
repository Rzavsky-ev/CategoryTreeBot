package pro.sky.telegrambot.service;

import pro.sky.telegrambot.exception.InvalidExcelFormatException;
import pro.sky.telegrambot.model.Category;

import java.io.IOException;
import java.util.List;

/**
 * Сервис для работы с Excel-файлами категорий.
 * <p>
 * Предоставляет методы для:
 * <ul>
 *     <li>Генерации Excel-файла с текущим деревом категорий</li>
 *     <li>Парсинга Excel-файла для восстановления дерева категорий</li>
 * </ul>
 * <p>
 * Формат Excel-файла:
 * <ul>
 *     <li>Столбец "id_Категории" - уникальный идентификатор</li>
 *     <li>Столбец "Имя_Категории" - название категории</li>
 *     <li>Столбец "id_Родителя" - ссылка на родительскую категорию (может быть пустым)</li>
 * </ul>
 */
public interface ExcelProcessingService {

    /**
     * Генерирует Excel-файл с текущей структурой категорий.
     *
     * @return массив байтов с содержимым Excel-файла
     * @throws IOException если произошла ошибка при генерации файла
     */
    byte[] generateCategoriesExcel() throws IOException;

    /**
     * Парсит Excel-файл и преобразует его в список категорий.
     *
     * @param fileContent содержимое Excel-файла в виде массива байтов
     * @return список категорий, восстановленных из файла
     * @throws IOException                 если произошла ошибка при чтении файла
     * @throws InvalidExcelFormatException если файл имеет неверный формат
     */
    List<Category> parseExcel(byte[] fileContent) throws IOException;

}
