package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.command.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис для обработки и выполнения команд Telegram-бота.
 * Обрабатывает текстовые команды и загружаемые документы, делегируя выполнение конкретным командам.
 */
@Service
public class CommandServiceImpl implements CommandService {

    private final Map<NamesCommand, Command> commands;

    private final TelegramBot telegramBot;

    private final UploadCommand uploadCommand;

    private Logger logger = LoggerFactory.getLogger(CommandServiceImpl.class);

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

    public void processCommand(Update update) {
        if (update.message() != null) {
            Message message = update.message();
            Long chatId = message.chat().id();
            try {
                if (update.message().document() != null) {
                    handleDocument(message, chatId);
                }
                if (message.text() != null && message.text().startsWith("/")) {
                    handleText(message, chatId);
                }
            } catch (IOException e) {
                logger.error("Error processing update: {}", update, e);
                telegramBot.execute(new SendMessage(chatId, "Произошла непредвиденная ошибка " +
                        "при обработку Excel документа."));
            } catch (Exception e) {
                logger.error("Error processing update: {}", update, e);
                telegramBot.execute(new SendMessage(chatId, "Произошла непредвиденная ошибка при обработке запроса."));
            }
        }
    }

    /**
     * Обрабатывает загруженный документ (Excel-файл с категориями).
     *
     * @param message сообщение от пользователя
     * @param chatId  ID чата для отправки ответа
     * @throws IOException если произошла ошибка при обработке файла
     */
    private void handleDocument(Message message, Long chatId) throws IOException {
        uploadCommand.handleDocument(chatId, message);
    }

    /**
     * Обрабатывает текстовую команду от пользователя.
     *
     * @param message сообщение от пользователя
     * @param chatId  ID чата для отправки ответа
     */
    private void handleText(Message message, Long chatId) {
        String commandName = message.text().split(" ")[0];
        NamesCommand namesCommand = NamesCommand.fromString(commandName).
                orElse(null);
        if (namesCommand != null) {
            Command command = commands.get(namesCommand);
            SendMessage response = command.execute(chatId, message.text());
            telegramBot.execute(response);
        } else {
            telegramBot.execute(new SendMessage(chatId, "Я понимаю только команды. Введите " +
                    "/help для справки."));
        }
    }
}
