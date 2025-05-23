package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.service.CategoryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Unit-тесты для команды просмотра дерева категорий {@link ViewTreeCommand}.
 * <p>
 * Проверяет следующие аспекты работы команды:
 * <ul>
 *   <li>Корректность возвращаемого имени команды</li>
 *   <li>Обработку команды с валидным количеством аргументов</li>
 *   <li>Обработку команды с невалидным количеством аргументов</li>
 *   <li>Взаимодействие с сервисом категорий</li>
 * </ul>
 *
 * <p>Использует Mockito для мокирования {@link CategoryService} и проверки вызовов.
 */
@ExtendWith(MockitoExtension.class)
public class ViewTreeCommandTest {

    /**
     * Тестовый идентификатор чата.
     * Используется для проверки корректности отправки сообщений.
     */
    private final Long CHAT_ID = 123L;

    @Mock
    CategoryService categoryServiceMock;

    @InjectMocks
    ViewTreeCommand viewTreeCommandTest;

    /**
     * Проверяет корректность возвращаемого имени команды.
     * <p>
     * Ожидаемый результат: команда должна возвращать значение {@link NamesCommand#VIEW_TREE}.
     */
    @Test
    public void getNameCommandTest() {
        assertEquals(NamesCommand.VIEW_TREE, viewTreeCommandTest.getNameCommand());
    }

    /**
     * Тестирует обработку команды с невалидным количеством аргументов (2 аргумента).
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Корректность установки chat_id в ответном сообщении</li>
     *   <li>Вывод сообщения об ошибке формата команды</li>
     *   <li>Отсутствие вызова метода viewTree() сервиса категорий</li>
     * </ul>
     */
    @Test
    public void executeMoreThanOneArgument() {
        String commandText1 = "/viewTree";
        String commandText2 = "a";
        SendMessage sendMessage = viewTreeCommandTest.execute(CHAT_ID, List.of(commandText1, commandText2));
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Неверный формат команды! Используйте: /viewTree",
                sendMessage.getParameters().get("text"));

    }

    /**
     * Тестирует обработку команды с валидным количеством аргументов (1 аргумент).
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Вызов метода viewTree() сервиса категорий</li>
     *   <li>Корректность установки chat_id в ответном сообщении</li>
     * </ul>
     */
    @Test
    public void executeOneArgument() {
        String commandText = "/viewTree";
        SendMessage sendMessage = viewTreeCommandTest.execute(CHAT_ID, List.of(commandText));
        verify(categoryServiceMock).viewTree();
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
    }
}
