package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.exception.InvalidExcelFormatException;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.CategoryRepository;
import pro.sky.telegrambot.service.ExcelProcessingService;

import java.io.IOException;
import java.util.List;

/**
 * Команда для загрузки категорий из Excel-файла в систему.
 * Обрабатывает команду /upload и загруженные файлы с данными категорий.
 */
@Component
public class UploadCommand implements Command {


    private final ExcelProcessingService workingWithExcelService;

    private final TelegramBot telegramBot;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final CategoryRepository categoryRepository;

    public UploadCommand(ExcelProcessingService workingWithExcelService,
                         TelegramBot telegramBot, CategoryRepository categoryRepository) {
        this.workingWithExcelService = workingWithExcelService;
        this.telegramBot = telegramBot;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public NamesCommand getNameCommand() {
        return NamesCommand.UPLOAD;
    }

    @Override
    public SendMessage execute(Long chatId, String commandText) {
        return new SendMessage(chatId,
                "Пожалуйста, отправьте Excel-файл с категориями.");
    }

    /**
     * Обрабатывает загруженный Excel-файл с категориями.
     *
     * @param chatId  ID чата для отправки ответа
     * @param message сообщение от пользователя
     */
    public void handleDocumentExcel(Long chatId, Message message) {
        try {
            byte[] fileContent = downloadFile(message);
            List<Category> categories = workingWithExcelService.parseExcel(fileContent);
            saveCategories(categories);
            telegramBot.execute(new SendMessage(chatId, "Категории успешно загружены!"));
        } catch (IOException e) {
            telegramBot.execute(new SendMessage(chatId,
                    "Ошибка чтения файла: " + e.getMessage()));
        } catch (InvalidExcelFormatException e) {
            telegramBot.execute(new SendMessage(chatId,
                    "Неверный формат Excel-файла: " + e.getMessage()));
        } catch (Exception e) {
            telegramBot.execute(new SendMessage(chatId,
                    "Ошибка при загрузке категорий: " + e.getMessage()));
        }
    }

    /**
     * Скачивает содержимое файла из Telegram.
     *
     * @param message сообщение от пользователя
     * @return содержимое файла в виде массива байтов
     * @throws IOException если произошла ошибка при загрузке файла
     */
    private byte[] downloadFile(Message message) throws IOException {
        String fileId = message.document().fileId();
        File file = telegramBot.execute(new GetFile(fileId)).file();
        return telegramBot.getFileContent(file);
    }

    /**
     * Сохраняет список категорий в базу данных с установкой связей между ними.
     *
     * @param categories список категорий для сохранения
     */
    @Transactional
    private void saveCategories(List<Category> categories) {
        categoryRepository.deleteAll();
        categoryRepository.saveAll(categories);

        categories.stream()
                .filter(c -> c.getParent() != null && c.getParent().getId() != null)
                .forEach(c -> {
                    Category parent = categoryRepository.findById(c.getParent().getId()).orElse(null);
                    if (parent != null) {
                        c.setParent(parent);
                        categoryRepository.save(c);
                    }
                });
    }
}


