package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тестовый класс для проверки функциональности команды загрузки.
 * Проверяет различные сценарии выполнения команды {@link DownloadCommand},
 * включая обработку корректных и некорректных входных данных.
 *
 * <p>Использует Mockito для создания моков зависимостей.</p>
 *
 * @see DownloadCommand
 * @see MockitoExtension
 */
@ExtendWith(MockitoExtension.class)
public class DownloadCommandTest {

    /**
     * Идентификатор тестового чата.
     */
    private final Long CHAT_ID = 123L;

    @InjectMocks
    DownloadCommand downloadCommandTest;

    /**
     * Проверяет, что команда возвращает корректное имя.
     * Ожидается, что имя команды соответствует {@link NamesCommand#DOWNLOAD}.
     */
    @Test
    public void getNameCommandTest() {
        assertEquals(NamesCommand.DOWNLOAD, downloadCommandTest.getNameCommand());
    }

    /**
     * Проверяет обработку команды с избыточным количеством аргументов.
     * Ожидается возвращение сообщения об ошибке с инструкцией по использованию.
     */
    @Test
    public void executeMoreThanOneArgument() {
        String commandText1 = "/";
        String commandText2 = "a";
        SendMessage sendMessage = downloadCommandTest.execute(CHAT_ID, List.of(commandText1, commandText2));
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Неверный формат команды! Используйте: /download",
                sendMessage.getParameters().get("text"));
    }

    /**
     * Проверяет обработку команды с корректным количеством аргументов.
     * Ожидается успешное выполнение команды и возвращение сообщения с указанным chat_id.
     */
    @Test
    public void executeOneArgument() {
        String commandText = "/download";
        SendMessage sendMessage = downloadCommandTest.execute(CHAT_ID, List.of(commandText));
        assertEquals(CHAT_ID, sendMessage
                .getParameters().get("chat_id"));
    }
}
