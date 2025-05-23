package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.command.*;
import pro.sky.telegrambot.exception.ElementNameQuotesRequiredException;
import pro.sky.telegrambot.repository.CategoryRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Реализация сервиса обработки команд Telegram бота.
 * <p>
 * Обрабатывает входящие обновления и делегирует выполнение соответствующим обработчикам команд.
 * Поддерживает два типа входящих сообщений:
 * <ul>
 *     <li>Текстовые команды (начинающиеся с /)</li>
 *     <li>Документы (Excel файлы для загрузки категорий)</li>
 * </ul>
 *
 * @see Service Аннотация Spring, обозначающая класс как сервис
 * @see CommandService Интерфейс, который реализует данный сервис
 */
@Service
public class CommandServiceImpl implements CommandService {

    /**
     * Мапа зарегистрированных команд (ключ - имя команды, значение - обработчик)
     */
    private final Map<NamesCommand, Command> commands;

    private final TelegramBot telegramBot;

    private final UploadCommand uploadCommand;

    private Logger logger = LoggerFactory.getLogger(CommandServiceImpl.class);

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param telegramBot клиент Telegram бота
     * @param commandList список всех доступных команд
     * @param uploadCommand обработчик команды загрузки
     */
    public CommandServiceImpl(TelegramBot telegramBot, List<Command> commandList, UploadCommand uploadCommand) {
        this.telegramBot = telegramBot;
        this.commands = commandList.stream()
                .collect(Collectors.toMap(
                        Command::getNameCommand,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
        this.uploadCommand = uploadCommand;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Обрабатывает входящее обновление:
     * <ul>
     *     <li>Для документов - вызывает обработчик загрузки</li>
     *     <li>Для текстовых команд - парсит аргументы и вызывает соответствующий обработчик</li>
     *     <li>Для некорректных сообщений - отправляет подсказку</li>
     * </ul>
     *
     * @param update входящее обновление от Telegram API
     */
    public void processCommand(Update update) {
        if (update.message() != null) {
            Message message = update.message();
            Long chatId = message.chat().id();
            try {
                if (update.message().document() != null) {
                    handleDocument(message, chatId);
                } else if (message.text() != null && message.text().startsWith("/")) {
                    handleText(message, chatId);
                } else {
                    telegramBot.execute(new SendMessage(chatId, "Я понимаю только команды. Введите " +
                            "/help для справки."));
                }
            } catch (ElementNameQuotesRequiredException e) {
                telegramBot.execute(new SendMessage(chatId, "Ошибка: " + e.getMessage()));
            } catch (IOException e) {
                logger.error("Ошибка обработки обновления: {}", update, e);
                telegramBot.execute(new SendMessage(chatId, "Произошла непредвиденная ошибка " +
                        "при обработку Excel документа."));
            } catch (Exception e) {
                logger.error("Ошибка обработки обновления: {}", update, e);
                telegramBot.execute(new SendMessage(chatId, "Произошла непредвиденная ошибка при обработке запроса."));
            }
        }
    }

    /**
     * Обрабатывает входящий документ (Excel файл).
     *
     * @param message сообщение с документом
     * @param chatId идентификатор чата
     * @throws IOException если произошла ошибка при обработке файла
     */
    private void handleDocument(Message message, Long chatId) throws IOException {
        uploadCommand.handleDocumentExcel(chatId, message);
    }

    /**
     * Обрабатывает текстовую команду.
     *
     * @param message текстовое сообщение с командой
     * @param chatId идентификатор чата
     */
    private void handleText(Message message, Long chatId) {
        List<String> arguments = parseMessageArguments(message.text());
        String commandName = arguments.get(0);
        NamesCommand namesCommand = NamesCommand.fromString(commandName).
                orElse(null);
        if (namesCommand != null) {
            Command command = commands.get(namesCommand);
            SendMessage response = command.execute(chatId, arguments);
            telegramBot.execute(response);
        } else {
            telegramBot.execute(new SendMessage(chatId, "Я понимаю только команды. Введите " +
                    "/help для справки."));
        }
    }

    /**
     * Парсит аргументы команды из текста сообщения.
     * <p>
     * Аргументы в кавычках обрабатываются как единое целое.
     *
     * @param messageText текст сообщения для парсинга
     * @return список аргументов (первый элемент - имя команды)
     * @throws ElementNameQuotesRequiredException если элементы не заключены в кавычки
     */
    private List<String> parseMessageArguments(String messageText) {
        List<String> arguments = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"'\\s]+|\"[^\"]*\"|'[^']*')").matcher(messageText.trim());

        while (m.find()) {
            String arg = m.group(1);
            if (arguments.isEmpty()) {
                arguments.add(arg);
                continue;
            }
            if (!(arg.startsWith("\"") && arg.endsWith("\"")) &&
                    !(arg.startsWith("'") && arg.endsWith("'"))) {
                throw new ElementNameQuotesRequiredException(
                        "Элемент \"" + arg + "\" должен быть заключен в кавычки.\n" +
                                "Пример: /addElement \"" + arg + "\"");
            }
            arguments.add(arg.substring(1, arg.length() - 1));
        }
        return arguments;
    }
}
