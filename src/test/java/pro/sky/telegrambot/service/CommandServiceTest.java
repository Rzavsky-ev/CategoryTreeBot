package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.command.Command;
import pro.sky.telegrambot.command.NamesCommand;
import pro.sky.telegrambot.command.UploadCommand;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


/**
 * Unit-тесты для {@link CommandServiceImpl}, проверяющие обработку входящих команд.
 * <p>
 * Тесты покрывают следующие сценарии:
 * <ul>
 *   <li>Обработку обновлений без сообщений</li>
 *   <li>Обработку документов</li>
 *   <li>Обработку текстовых команд</li>
 *   <li>Обработку обычного текста (не команд)</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class CommandServiceTest {

    @Mock
    private TelegramBot telegramBotMock;

    @Mock
    private UploadCommand uploadCommandMock;

    @Mock
    private Command helpCommandMock;

    @Mock
    private Update updateMock;

    @Mock
    private Message messageMock;

    @Mock
    private Chat chatMock;

    CommandServiceImpl commandServiceTest;

    /**
     * Тестовый идентификатор чата.
     */
    private final Long CHAT_ID = 123L;

    /**
     * Настройка тестового окружения перед каждым тестом.
     * <p>
     * Инициализирует тестируемый сервис с mock-зависимостями.
     */
    @BeforeEach
    public void setUp() {
        when(helpCommandMock.getNameCommand()).thenReturn(NamesCommand.HELP);

        commandServiceTest = new CommandServiceImpl(
                telegramBotMock,
                List.of(helpCommandMock),
                uploadCommandMock
        );
    }

    /**
     * Тестирует обработку обновления без сообщения.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Отсутствие взаимодействия с TelegramBot</li>
     *   <li>Отсутствие вызовов команд</li>
     * </ul>
     */
    @Test
    public void processCommandIgnoresUpdateWithoutMessage() {
        when(updateMock.message()).thenReturn(null);

        commandServiceTest.processCommand(updateMock);

        verifyNoInteractions(telegramBotMock, uploadCommandMock);
    }

    /**
     * Тестирует обработку сообщения с документом.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Вызов метода handleDocumentExcel команды загрузки</li>
     *   <li>Передачу правильных параметров (chat_id и сообщение)</li>
     * </ul>
     */
    @Test
    public void processingCommandWhenDocumentIsPresentCallsHandleDocument() throws IOException {
        when(chatMock.id()).thenReturn(CHAT_ID);
        when(messageMock.chat()).thenReturn(chatMock);
        when(messageMock.document()).thenReturn(new Document());
        when(updateMock.message()).thenReturn(messageMock);

        commandServiceTest.processCommand(updateMock);

        verify(uploadCommandMock).handleDocumentExcel(eq(CHAT_ID), eq(messageMock));
    }

    /**
     * Тестирует обработку сообщения без документа.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Отсутствие вызова handleDocumentExcel</li>
     * </ul>
     */
    @Test
    public void processingCommandWhenDocumentNotPresentNotCallsHandleDocument() throws IOException {
        when(chatMock.id()).thenReturn(CHAT_ID);
        when(messageMock.chat()).thenReturn(chatMock);
        when(messageMock.document()).thenReturn(null);
        when(updateMock.message()).thenReturn(messageMock);

        commandServiceTest.processCommand(updateMock);

        verify(uploadCommandMock, never()).handleDocumentExcel(eq(CHAT_ID), eq(messageMock));
    }

    /**
     * Тестирует обработку текстовой команды.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Вызов соответствующей команды (help)</li>
     *   <li>Отправку ответного сообщения через TelegramBot</li>
     *   <li>Передачу правильных параметров команде</li>
     * </ul>
     */
    @Test
    public void processingCommandWhenTextIsPresentCallsHandleText() {
        when(chatMock.id()).thenReturn(CHAT_ID);
        when(messageMock.chat()).thenReturn(chatMock);
        when(messageMock.text()).thenReturn("/help");
        when(updateMock.message()).thenReturn(messageMock);

        SendMessage expectedResponse = new SendMessage(CHAT_ID.toString(), "Test");

        when(helpCommandMock.execute(eq(CHAT_ID), eq(List.of("/help")))).thenReturn(expectedResponse);

        commandServiceTest.processCommand(updateMock);
        verify(helpCommandMock).execute(eq(CHAT_ID), eq(List.of("/help")));

        verify(telegramBotMock).execute(eq(expectedResponse));
    }

    /**
     * Тестирует обработку сообщения без текста.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Отсутствие вызова команд</li>
     * </ul>
     */
    @Test
    public void processingCommandWhenTextNotPresentNotCallsHandleText() {
        when(chatMock.id()).thenReturn(CHAT_ID);
        when(messageMock.chat()).thenReturn(chatMock);
        when(messageMock.document()).thenReturn(null);
        when(messageMock.text()).thenReturn(null);
        when(updateMock.message()).thenReturn(messageMock);

        commandServiceTest.processCommand(updateMock);

        verifyNoInteractions(uploadCommandMock);
    }

    /**
     * Тестирует обработку обычного текста (не команды).
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Отправку сообщения с инструкцией</li>
     *   <li>Корректность содержимого сообщения</li>
     *   <li>Правильность указания chat_id</li>
     * </ul>
     */
    @Test
    public void processingCommandWhenTextIsPresentNotCallsHandleText() {
        when(chatMock.id()).thenReturn(CHAT_ID);
        when(messageMock.chat()).thenReturn(chatMock);
        when(messageMock.text()).thenReturn("Test");
        when(updateMock.message()).thenReturn(messageMock);

        String textMessage = "Я понимаю только команды. Введите " +
                "/help для справки.";

        commandServiceTest.processCommand(updateMock);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBotMock).execute(messageCaptor.capture());

        SendMessage sendMessage = messageCaptor.getValue();
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals(textMessage, sendMessage.getParameters().get("text"));
    }
}
