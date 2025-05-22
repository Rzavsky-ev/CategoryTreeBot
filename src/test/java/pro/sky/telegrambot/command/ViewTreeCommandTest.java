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
 * Тестовый класс для команды просмотра дерева категорий (ViewTreeCommand).
 * Проверяет:
 * - корректность возвращаемого имени команды
 * - обработку команды с неверным количеством аргументов
 * - вызов метода просмотра дерева категорий
 */
@ExtendWith(MockitoExtension.class)
public class ViewTreeCommandTest {

    private final Long CHAT_ID = 123L;

    @Mock
    CategoryService categoryServiceMock;

    @InjectMocks
    ViewTreeCommand viewTreeCommandTest;

    @Test
    public void getNameCommandTest() {
        assertEquals(NamesCommand.VIEW_TREE, viewTreeCommandTest.getNameCommand());
    }

    @Test
    public void executeMoreThanOneArgument() {
        String commandText = "/ a s d";
        SendMessage sendMessage = viewTreeCommandTest.execute(CHAT_ID, commandText);
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Неверный формат команды! Используйте: /viewTree",
                sendMessage.getParameters().get("text"));

    }

    @Test
    public void executeOneArgument() {
        String commandText = "/viewTree";
        SendMessage sendMessage = viewTreeCommandTest.execute(CHAT_ID, commandText);
        verify(categoryServiceMock).viewTree();
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
    }
}
