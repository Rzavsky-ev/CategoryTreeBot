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
 * Модульные тесты для класса {@link CommandServiceImpl}.
 * <p>
 * Этот тестовый класс проверяет поведение {@code CommandServiceImpl} при обработке различных типов Telegram-обновлений,
 * включая обновления с документами, текстовыми командами и невалидными/пустыми данными. Для мокирования зависимостей используется Mockito.
 * </p>
 *
 * <p>Основные тестируемые сценарии:</p>
 * <ul>
 *   <li>Игнорирование обновлений без сообщений</li>
 *   <li>Обработка обновлений с документами</li>
 *   <li>Обработка текстовых команд (например, "/help")</li>
 *   <li>Обработка обновлений без текста или документов</li>
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

    private final Long CHAT_ID = 123L;

    @BeforeEach
    void setUp() {
        when(helpCommandMock.getNameCommand()).thenReturn(NamesCommand.HELP);

        commandServiceTest = new CommandServiceImpl(
                telegramBotMock,
                List.of(helpCommandMock),
                uploadCommandMock
        );
    }

    @Test
    public void processCommandIgnoresUpdateWithoutMessage() {
        when(updateMock.message()).thenReturn(null);

        commandServiceTest.processCommand(updateMock);

        verifyNoInteractions(telegramBotMock, uploadCommandMock);
    }

    @Test
    public void processingCommandWhenDocumentIsPresentCallsHandleDocument() throws IOException {
        when(chatMock.id()).thenReturn(CHAT_ID);
        when(messageMock.chat()).thenReturn(chatMock);
        when(messageMock.document()).thenReturn(new Document());
        when(updateMock.message()).thenReturn(messageMock);

        commandServiceTest.processCommand(updateMock);

        verify(uploadCommandMock).handleDocumentExcel(eq(CHAT_ID), eq(messageMock));
    }

    @Test
    public void processingCommandWhenDocumentNotPresentNotCallsHandleDocument() throws IOException {
        when(chatMock.id()).thenReturn(CHAT_ID);
        when(messageMock.chat()).thenReturn(chatMock);
        when(messageMock.document()).thenReturn(null);
        when(updateMock.message()).thenReturn(messageMock);

        commandServiceTest.processCommand(updateMock);

        verify(uploadCommandMock, never()).handleDocumentExcel(eq(CHAT_ID), eq(messageMock));
    }

    @Test
    void processingCommandWhenTextIsPresentCallsHandleText() {
        when(chatMock.id()).thenReturn(CHAT_ID);
        when(messageMock.chat()).thenReturn(chatMock);
        when(messageMock.text()).thenReturn("/help");
        when(updateMock.message()).thenReturn(messageMock);

        SendMessage expectedResponse = new SendMessage(CHAT_ID.toString(), "Test");

        when(helpCommandMock.execute(eq(CHAT_ID), eq("/help"))).thenReturn(expectedResponse);

        commandServiceTest.processCommand(updateMock);
        verify(helpCommandMock).execute(eq(CHAT_ID), eq("/help"));

        verify(telegramBotMock).execute(eq(expectedResponse));
    }

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

    @Test
    void processingCommandWhenTextIsPresentNotCallsHandleText() {
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
