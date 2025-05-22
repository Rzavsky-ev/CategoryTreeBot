package pro.sky.telegrambot.command;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.exception.CategoryTreeIsEmptyException;
import pro.sky.telegrambot.exception.ErrorSendingFileException;
import pro.sky.telegrambot.service.ExcelProcessingService;


import java.io.IOException;

/**
 * Команда для скачивания Excel-файла с категориями в формате .xlsx.
 * Обрабатывает команду /download и отправляет файл с деревом категорий.
 */
@Component
public class DownloadCommand implements Command {

    private static final long MAX_FILE_SIZE = 50_000_000L;

    private static final String DEFAULT_FILENAME = "Categories.xlsx";

    private static final String DEFAULT_CAPTION = "Дерево категорий";

    private final ExcelProcessingService excelProcessingService;

    private final TelegramBot telegramBot;

    public DownloadCommand(ExcelProcessingService workingWithExcelService,
                           TelegramBot telegramBot) {
        this.excelProcessingService = workingWithExcelService;
        this.telegramBot = telegramBot;
    }

    @Override
    public NamesCommand getNameCommand() {
        return NamesCommand.DOWNLOAD;
    }

    @Override
    public SendMessage execute(Long chatId, String commandText) {
        String[] arguments = commandText.trim().split("\\s+");
        if (arguments.length > 1) {
            return new SendMessage(chatId,
                    "Неверный формат команды! Используйте: /download");
        }
        try {
            sendExcelDocument(chatId);
            return new SendMessage(chatId, "Файл с категориями отправлен.");
        } catch (CategoryTreeIsEmptyException e) {
            return new SendMessage(chatId, "Ошибка: " + e.getMessage());
        } catch (IOException e) {
            return new SendMessage(chatId, "Ошибка отправки документа: " + e.getMessage());
        } catch (Exception e) {
            return new SendMessage(chatId,
                    "Произошла непредвиденная ошибка при отправки документа.");
        }
    }

    /**
     * Генерирует и отправляет Excel-файл с категориями.
     *
     * @param chatId ID чата для отправки
     * @throws IOException               при ошибках генерации файла
     * @throws ErrorSendingFileException при ошибках отправки файла
     */
    private void sendExcelDocument(Long chatId) throws IOException, ErrorSendingFileException {
        byte[] excelData = excelProcessingService.generateCategoriesExcel();

        if (excelData.length > MAX_FILE_SIZE) {
            throw new ErrorSendingFileException("Файл слишком большой для отправки");
        }

        SendDocument request = new SendDocument(chatId, excelData)
                .fileName(DEFAULT_FILENAME)
                .caption(DEFAULT_CAPTION);

        SendResponse response = telegramBot.execute(request);

        if (!response.isOk()) {
            throw new ErrorSendingFileException(
                    "Ошибка отправки файла: " + response.description());
        }
    }
}
