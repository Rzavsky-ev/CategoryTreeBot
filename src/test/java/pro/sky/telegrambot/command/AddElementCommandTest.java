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
 * Тестовый класс для проверки функциональности команды добавления элемента.
 * Проверяет различные сценарии выполнения команды {@link AddElementCommand},
 * включая обработку корректных и некорректных входных данных.
 *
 * <p>Использует Mockito для создания моков зависимостей и проверки взаимодействий.</p>
 *
 * @see AddElementCommand
 * @see MockitoExtension
 * @see CategoryService
 */
@ExtendWith(MockitoExtension.class)
public class AddElementCommandTest {

    /**
     * Идентификатор тестового чата.
     */
    private final Long CHAT_ID = 123L;

    @Mock
    CategoryService categoryServiceMock;

    @InjectMocks
    AddElementCommand addElementCommandTest;

    /**
     * Проверяет, что команда возвращает корректное имя.
     */
    @Test
    public void getNameCommandTest() {
        assertEquals(NamesCommand.ADD_ELEMENT, addElementCommandTest.getNameCommand());
    }

    /**
     * Проверяет обработку команды с недостаточным количеством аргументов.
     * Ожидается возвращение сообщения об ошибке с инструкцией по использованию.
     */
    @Test
    public void executeLessThanTwoArguments() {
        String commandText = "/";
        SendMessage sendMessage = addElementCommandTest.execute(CHAT_ID, List.of(commandText));
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("""
                            ⚠ *Ошибка формата команды!*
                            Правильное использование:
                         **Добавить корневую категорию:**
                           /addElement "Название элемента"
                         **Добавить дочернюю категорию:**
                           /addElement "Родительский элемент" "Дочерний элемент"
                        ❗ *Не забудьте кавычки!* ❗""",
                sendMessage.getParameters().get("text"));
    }

    /**
     * Проверяет обработку команды с избыточным количеством аргументов.
     * Ожидается возвращение сообщения об ошибке с инструкцией по использованию.
     */
    @Test
    public void executeMoreThanThreeArguments() {
        String commandText = "/a";

        SendMessage sendMessage = addElementCommandTest.execute(CHAT_ID, List.of(commandText, commandText
                , commandText, commandText));
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("""
                            ⚠ *Ошибка формата команды!*
                            Правильное использование:
                         **Добавить корневую категорию:**
                           /addElement "Название элемента"
                         **Добавить дочернюю категорию:**
                           /addElement "Родительский элемент" "Дочерний элемент"
                        ❗ *Не забудьте кавычки!* ❗""",
                sendMessage.getParameters().get("text"));
    }

    /**
     * Проверяет успешное добавление корневой категории.
     * Ожидается вызов соответствующего метода сервиса и возвращение сообщения об успешном добавлении.
     */
    @Test
    public void executeTwoArguments() {
        String commandText1 = "/addElement";
        String commandText2 = "a";
        SendMessage sendMessage = addElementCommandTest.execute(CHAT_ID, List.of(commandText1,
                commandText2));
        verify(categoryServiceMock).addRootCategory("a");
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Корневой элемент \"a\" добавлен.",
                sendMessage.getParameters().get("text"));
    }

    /**
     * Проверяет успешное добавление дочерней категории.
     * Ожидается вызов соответствующего метода сервиса и возвращение сообщения об успешном добавлении.
     */
    @Test
    public void executeThreeArguments() {
        String commandText1 = "/addElement";
        String commandText2 = "a";
        String commandText3 = "b";
        SendMessage sendMessage = addElementCommandTest.execute(CHAT_ID, List.of(commandText1,
                commandText2, commandText3));
        verify(categoryServiceMock).addChildCategory("a", "b");
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Дочерний элемент \"b\" добавлен.",
                sendMessage.getParameters().get("text"));
    }
}
