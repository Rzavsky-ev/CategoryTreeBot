package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Unit-тесты для {@link UploadCommand} - команды загрузки Excel-файлов с категориями.
 * <p>
 * Тесты покрывают следующие аспекты работы команды:
 * <ul>
 *   <li>Корректность возвращаемого имени команды</li>
 *   <li>Поведение при вызове команды /upload</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class UploadCommandTest {

    private final Long CHAT_ID = 123L;

    @Mock
    TelegramBot telegramBotMock;

    @InjectMocks
    UploadCommand uploadCommandTest;

    @Test
    public void getNameCommandTest() {
        assertEquals(NamesCommand.UPLOAD, uploadCommandTest.getNameCommand());
    }

    @Test
    public void executeTest() {
        String commandText = "/upload";
        String message = "Пожалуйста, отправьте Excel-файл с категориями.";
        SendMessage sendMessage = uploadCommandTest.execute(CHAT_ID, commandText);
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals(message, sendMessage.getParameters().get("text"));
    }

    @Test
    public void handleDocumentExcelTest() {
        Message message = new Message();

        uploadCommandTest.handleDocumentExcel(CHAT_ID, message);
        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBotMock).execute(messageCaptor.capture());

        SendMessage sendMessage = messageCaptor.getValue();
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));

    }
}
