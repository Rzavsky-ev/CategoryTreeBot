package pro.sky.telegrambot.service;

import pro.sky.telegrambot.model.Category;

import java.io.IOException;
import java.util.List;

/**
 * Сервис для работы с Excel-файлами категорий.
 */
public interface ExcelProcessingService {

    /**
     * Генерирует Excel-файл с текущей структурой категорий.
     *
     * @return массив байтов с содержимым Excel-файла
     */
    byte[] generateCategoriesExcel() throws IOException;

    /**
     * Парсит Excel-файл и преобразует его в список категорий.
     *
     * @param fileContent содержимое Excel-файла в виде массива байтов
     * @return список категорий, восстановленных из файла
     */
    List<Category> parseExcel(byte[] fileContent) throws IOException;
}
