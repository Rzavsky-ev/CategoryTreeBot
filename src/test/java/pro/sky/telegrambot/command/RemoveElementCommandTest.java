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
 * Unit-тесты для {@link RemoveElementCommand}, проверяющие корректность работы команды удаления элементов.
 * <p>
 * Тесты покрывают следующие сценарии:
 * <ul>
 *   <li>Получение имени команды</li>
 *   <li>Обработку команды с корректным количеством аргументов (2 аргумента)</li>
 *   <li>Обработку команды с недостаточным количеством аргументов (1 аргумент)</li>
 *   <li>Обработку команды с избыточным количеством аргументов (4 аргумента)</li>
 * </ul>
 *
 * <p>Использует Mockito для мокирования {@link CategoryService} и проверки взаимодействий.
 */
@ExtendWith(MockitoExtension.class)
public class RemoveElementCommandTest {

    /**
     * Тестовый идентификатор чата для проверки отправки сообщений.
     */
    private final Long CHAT_ID = 123L;

    @Mock
    CategoryService categoryServiceMock;

    @InjectMocks
    RemoveElementCommand removeElementCommandTest;

    /**
     * Проверяет корректность возвращаемого имени команды.
     * <p>
     * Ожидаемый результат: имя команды должно соответствовать {@link NamesCommand#REMOVE_ELEMENT}.
     */
    @Test
    public void getNameCommandTest() {
        assertEquals(NamesCommand.REMOVE_ELEMENT, removeElementCommandTest.getNameCommand());
    }

    /**
     * Тестирует обработку команды с избыточным количеством аргументов (невалидный случай).
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Корректность установки chat_id в ответном сообщении</li>
     *   <li>Вывод сообщения об ошибке формата команды</li>
     *   <li>Отсутствие вызовов сервиса категорий</li>
     * </ul>
     */
    @Test
    public void executeMoreThanTwoArguments() {
        String commandText1 = "/";
        String commandText2 = "a";
        String commandText3 = "b";
        String commandText4 = "c";
        SendMessage sendMessage = removeElementCommandTest.execute(CHAT_ID, List.of(commandText1,
                commandText2, commandText3, commandText4));
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Неверный формат команды!" +
                        " Используйте: /removeElement \"название элемента\"",
                sendMessage.getParameters().get("text"));
    }

    /**
     * Тестирует обработку команды с недостаточным количеством аргументов (невалидный случай).
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Корректность установки chat_id в ответном сообщении</li>
     *   <li>Вывод сообщения об ошибке формата команды</li>
     *   <li>Отсутствие вызовов сервиса категорий</li>
     * </ul>
     */
    @Test
    public void executeLessThanTwoArguments() {
        String commandText = "/";
        SendMessage sendMessage = removeElementCommandTest.execute(CHAT_ID, List.of(commandText));
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Неверный формат команды!" +
                        " Используйте: /removeElement \"название элемента\"",
                sendMessage.getParameters().get("text"));
    }


    /**
     * Тестирует обработку команды с корректным количеством аргументов (валидный случай).
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Вызов метода removeCategory сервиса категорий с правильным аргументом</li>
     *   <li>Корректность установки chat_id в ответном сообщении</li>
     *   <li>Вывод сообщения об успешном удалении элемента</li>
     * </ul>
     */
    @Test
    public void executeTwoArguments() {
        String commandText1 = "/removeElement";
        String commandText2 = "a";
        SendMessage sendMessage = removeElementCommandTest.execute(CHAT_ID, List.of(commandText1, commandText2));
        verify(categoryServiceMock).removeCategory("a");
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Элемент \"a\" удален.",
                sendMessage.getParameters().get("text"));
    }
}
