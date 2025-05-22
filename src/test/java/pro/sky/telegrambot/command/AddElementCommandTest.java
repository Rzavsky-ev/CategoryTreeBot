package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.service.CategoryService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Тестовый класс для проверки функциональности {@link AddElementCommand}.
 * Проверяет обработку команды добавления элементов с различными параметрами.
 */
@ExtendWith(MockitoExtension.class)
public class AddElementCommandTest {

    private final Long CHAT_ID = 123L;

    @Mock
    CategoryService categoryServiceMock;

    @InjectMocks
    AddElementCommand addElementCommandTest;

    @Test
    public void getNameCommandTest() {
        assertEquals(NamesCommand.ADD_ELEMENT, addElementCommandTest.getNameCommand());
    }

    @Test
    public void executeLessThanTwoArguments() {
        String commandText = "/";
        SendMessage sendMessage = addElementCommandTest.execute(CHAT_ID, commandText);
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Неверный формат команды! Используйте: /addElement <имя родительского элемента>" +
                        " <имя дочернего элемента>" +
                        " или /addElement <имя корневого элемента>",
                sendMessage.getParameters().get("text"));
    }

    @Test
    public void executeMoreThanThreeArguments() {
        String commandText = "/ a s d";
        SendMessage sendMessage = addElementCommandTest.execute(CHAT_ID, commandText);
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Неверный формат команды! Используйте: /addElement <имя родительского элемента>" +
                        " <имя дочернего элемента>" +
                        " или /addElement <имя корневого элемента>",
                sendMessage.getParameters().get("text"));
    }

    @Test
    public void executeTwoArguments() {
        String commandText = "/addElement a";
        SendMessage sendMessage = addElementCommandTest.execute(CHAT_ID, commandText);
        verify(categoryServiceMock).addRootCategory("a");
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Корневой элемент <<a>> добавлен.",
                sendMessage.getParameters().get("text"));
    }

    @Test
    public void executeThreeArguments() {
        String commandText = "/addElement a b";
        SendMessage sendMessage = addElementCommandTest.execute(CHAT_ID, commandText);
        verify(categoryServiceMock).addChildCategory("a", "b");
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Дочерний элемент <<b>> добавлен.",
                sendMessage.getParameters().get("text"));
    }
}
