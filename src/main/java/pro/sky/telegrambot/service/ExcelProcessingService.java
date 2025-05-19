package pro.sky.telegrambot.service;

import pro.sky.telegrambot.model.Category;

import java.io.IOException;
import java.util.List;

public interface ExcelProcessingService {

    byte[] generateCategoriesExcel() throws IOException;

    List<Category> parseExcel(byte[] fileContent) throws IOException;

}
