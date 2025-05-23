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
import java.util.List;

/**
 * Команда для скачивания дерева категорий в виде Excel-файла.
 * <p>
 * Позволяет пользователю получить полное дерево категорий в формате Excel.
 * Файл содержит структурированные данные о категориях и их взаимосвязях.
 * <p>
 * Формат команды: {@code /download}
 *
 * @see Command Базовый интерфейс команд
 * @see ExcelProcessingService Сервис для генерации Excel-файлов
 */
@Component
public class DownloadCommand implements Command {
    /**
     * Максимально допустимый размер файла для отправки (50 МБ)
     */
    private static final long MAX_FILE_SIZE = 50_000_000L;

    /**
     * Имя файла по умолчанию
     */
    private static final String DEFAULT_FILENAME = "Categories.xlsx";

    /**
     * Подпись к файлу по умолчанию
     */
    private static final String DEFAULT_CAPTION = "Дерево категорий";

    private final ExcelProcessingService excelProcessingService;

    private final TelegramBot telegramBot;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param excelProcessingService сервис для работы с Excel
     * @param telegramBot            клиент Telegram бота
     */
    public DownloadCommand(ExcelProcessingService excelProcessingService,
                           TelegramBot telegramBot) {
        this.excelProcessingService = excelProcessingService;
        this.telegramBot = telegramBot;
    }

    /**
     * Возвращает имя команды DOWNLOAD.
     *
     * @return имя команды
     */
    @Override
    public NamesCommand getNameCommand() {
        return NamesCommand.DOWNLOAD;
    }

    /**
     * Выполняет команду скачивания файла с категориями.
     * <p>
     * Генерирует Excel-файл с деревом категорий и отправляет его пользователю.
     * В случае успеха возвращает подробное описание структуры файла.
     *
     * @param chatId    идентификатор чата для отправки сообщения
     * @param arguments аргументы команды
     * @return SendMessage с результатом выполнения или сообщением об ошибке
     */
    @Override
    public SendMessage execute(Long chatId, List<String> arguments) {
        if (arguments.size() > 1) {
            return new SendMessage(chatId,
                    "Неверный формат команды! Используйте: /download");
        }
        try {
            sendExcelDocument(chatId);
            return new SendMessage(chatId,
                    """
                                📋 *Ваша таблица категорий готова!* 📋
                            
                            В этом файле представлена иерархия всех категорий товаров:
                            
                            🔸 *Столбец "id_Категории" - уникальный номер категории
                            🔸 *Столбец "Имя_Категории" - название категории
                            🔸 *Столбец "id_Родителя" - показывает к какой основной категории относится подкатегория
                            
                            📌 *Как читать таблицу:*
                            - Категории БЕЗ номера в столбце id_Родителя - это основные разделы (родительские)
                            - Категории С номером в столбце id_Родителя - это подразделы (дочерние)
                            
                            🔎 *Пример:*
                            Если в строке указано:
                            1 | Электроника | (пусто) - это главная категория
                            2 | Смартфоны | 1 - это подкатегория в разделе Электроника"""
            );
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
     * Генерирует и отправляет Excel-документ с деревом категорий.
     *
     * @param chatId идентификатор чата для отправки
     * @throws IOException                  если произошла ошибка при работе с файлом
     * @throws ErrorSendingFileException    если файл слишком большой или не может быть отправлен
     * @throws CategoryTreeIsEmptyException если дерево категорий пустое
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
