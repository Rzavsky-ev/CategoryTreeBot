package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.service.CommandService;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * Модульные тесты для класса {@link TelegramBotUpdatesListener}.
 * <p>
 * Проверяет обработку входящих сообщений телеграм-бота в различных сценариях.
 */
@ExtendWith(MockitoExtension.class)
public class TelegramBotUpdatesListenerTest {
    private final Long CHAT_ID = 123L;

    @Mock
    CommandService commandServiceMock;

    @InjectMocks
    TelegramBotUpdatesListener telegramBotUpdatesListenerTest;

    @Test
    void processMessageNotNullUpdate() {
        String text = "/help";
        Update update = createTestUpdate(text);

        List<Update> updates = Collections.singletonList(update);
        ;

        int result = telegramBotUpdatesListenerTest.process(updates);

        verify(commandServiceMock).processCommand(update);
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }

    @Test
    void processMessageNullUpdate() {
        Update update = null;

        List<Update> updates = Collections.singletonList(update);
        ;

        int result = telegramBotUpdatesListenerTest.process(updates);

        verifyNoMoreInteractions(commandServiceMock);
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }


    private static Update createTestUpdate(String text) {
        final Long CHAT_ID = 123L;
        Message message = new Message();
        setField(message, "text", text);

        Chat chat = new Chat();
        setField(chat, "id", CHAT_ID);
        setField(message, "chat", chat);

        Update update = new Update();
        setField(update, "update_id", 1);
        setField(update, "message", message);
        return update;
    }
}
