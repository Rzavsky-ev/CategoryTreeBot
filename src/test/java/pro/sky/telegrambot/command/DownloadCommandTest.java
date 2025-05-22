package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-тесты для класса {@link DownloadCommand}.
 * <p>
 * Тестирует функциональность команды загрузки файла с категориями, включая:
 * <ul>
 *   <li>Проверку имени команды</li>
 *   <li>Обработку команды с неверным количеством аргументов</li>
 *   <li>Успешное выполнение команды с корректными аргументами</li>
 * </ul>
 **/
@ExtendWith(MockitoExtension.class)
public class DownloadCommandTest {

    private final Long CHAT_ID = 123L;

    @InjectMocks
    DownloadCommand downloadCommandTest;

    @Test
    public void getNameCommandTest() {
        assertEquals(NamesCommand.DOWNLOAD, downloadCommandTest.getNameCommand());
    }

    @Test
    public void executeMoreThanOneArgument() {
        String commandText = "/ a";
        SendMessage sendMessage = downloadCommandTest.execute(CHAT_ID, commandText);
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Неверный формат команды! Используйте: /download",
                sendMessage.getParameters().get("text"));
    }

    @Test
    public void executeOneArgument() {
        String commandText = "/download";
        SendMessage sendMessage = downloadCommandTest.execute(CHAT_ID, commandText);
        assertEquals(CHAT_ID, sendMessage
                .getParameters().get("chat_id"));
    }
}
