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
 * Unit-тесты для {@link TelegramBotUpdatesListener}, проверяющие обработку входящих обновлений от Telegram.
 * <p>
 * Тесты покрывают следующие сценарии:
 * <ul>
 *   <li>Обработку валидного сообщения с текстом команды</li>
 *   <li>Обработку null-обновления</li>
 * </ul>
 *
 * <p>Использует:
 * <ul>
 *   <li>Mockito для мокирования {@link CommandService}</li>
 *   <li>ReflectionTestUtils для установки значений в final-поля Telegram API</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class TelegramBotUpdatesListenerTest {

    @Mock
    CommandService commandServiceMock;

    @InjectMocks
    TelegramBotUpdatesListener telegramBotUpdatesListenerTest;

    /**
     * Тестирует обработку валидного обновления с текстом команды.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Вызов метода processCommand() с правильным обновлением</li>
     *   <li>Возврат корректного статуса подтверждения (CONFIRMED_UPDATES_ALL)</li>
     * </ul>
     */
    @Test
    void processMessageNotNullUpdate() {
        String text = "/help";
        Update update = createTestUpdate(text);

        List<Update> updates = Collections.singletonList(update);

        int result = telegramBotUpdatesListenerTest.process(updates);

        verify(commandServiceMock).processCommand(update);
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }

    /**
     * Тестирует обработку null-обновления.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Отсутствие вызовов сервиса обработки команд</li>
     *   <li>Возврат корректного статуса подтверждения (CONFIRMED_UPDATES_ALL)</li>
     *   <li>Корректную обработку null-значений</li>
     * </ul>
     */
    @Test
    void processMessageNullUpdate() {
        Update update = null;

        List<Update> updates = Collections.singletonList(update);

        int result = telegramBotUpdatesListenerTest.process(updates);

        verifyNoMoreInteractions(commandServiceMock);
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, result);
    }

    /**
     * Создает тестовое обновление Telegram с указанным текстом сообщения.
     * <p>
     * Использует ReflectionTestUtils для установки значений в final-поля Telegram API,
     * которые недоступны для изменения стандартными средствами.
     *
     * @param text текст сообщения для тестового обновления
     * @return сконфигурированный объект Update
     */
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
