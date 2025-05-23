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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Unit-тесты для {@link UploadCommand}, проверяющие корректность работы команды загрузки файлов.
 * <p>
 * Тесты покрывают следующие сценарии:
 * <ul>
 *   <li>Получение имени команды</li>
 *   <li>Обработку команды с корректным количеством аргументов (1 аргумент)</li>
 *   <li>Обработку команды с некорректным количеством аргументов (2 аргумента)</li>
 *   <li>Обработку входящего Excel-файла</li>
 * </ul>
 *
 * <p>Использует Mockito для:
 * <ul>
 *   <li>Мокирования {@link TelegramBot}</li>
 *   <li>Перехвата и проверки отправляемых сообщений</li>
 *   <li>Проверки взаимодействий с Telegram API</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class UploadCommandTest {

    /**
     * Тестовый идентификатор чата для проверки отправки сообщений.
     */
    private final Long CHAT_ID = 123L;

    @Mock
    TelegramBot telegramBotMock;

    @InjectMocks
    UploadCommand uploadCommandTest;

    /**
     * Проверяет корректность возвращаемого имени команды.
     * <p>
     * Ожидаемый результат: имя команды должно соответствовать {@link NamesCommand#UPLOAD}.
     */
    @Test
    public void getNameCommandTest() {
        assertEquals(NamesCommand.UPLOAD, uploadCommandTest.getNameCommand());
    }

    /**
     * Тестирует обработку команды с корректным количеством аргументов (валидный случай).
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Корректность установки chat_id в ответном сообщении</li>
     *   <li>Вывод инструкции для пользователя о загрузке файла</li>
     * </ul>
     */
    @Test
    public void executeOneArgument() {
        String commandText = "/upload";
        String message = "Пожалуйста, отправьте Excel-файл с категориями.";
        SendMessage sendMessage = uploadCommandTest.execute(CHAT_ID, List.of(commandText));
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals(message, sendMessage.getParameters().get("text"));
    }

    /**
     * Тестирует обработку команды с некорректным количеством аргументов (невалидный случай).
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Корректность установки chat_id в ответном сообщении</li>
     *   <li>Вывод сообщения об ошибке формата команды</li>
     * </ul>
     */
    @Test
    public void executeTwoArguments() {
        String commandText1 = "/upload";
        String commandText2 = "a";
        String message = "Неверный формат команды! Используйте: /upload";
        SendMessage sendMessage = uploadCommandTest.execute(CHAT_ID, List.of(commandText1, commandText2));
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals(message, sendMessage.getParameters().get("text"));
    }

    /**
     * Тестирует обработку входящего Excel-документа.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Факт отправки сообщения через TelegramBot</li>
     *   <li>Корректность установки chat_id в отправляемом сообщении</li>
     *   <li>Использование ArgumentCaptor для перехвата отправляемого сообщения</li>
     * </ul>
     */
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
