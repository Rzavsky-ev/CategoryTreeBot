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
 * Тестовый класс для проверки функциональности команды удаления элементов ({@link RemoveElementCommand}).
 * Проверяет:
 * <ul>
 *   <li>Корректность возвращаемого имени команды</li>
 *   <li>Обработку команды с неверным количеством аргументов</li>
 *   <li>Удаление элемента при корректном формате команды</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class RemoveElementCommandTest {

    private final Long CHAT_ID = 123L;

    @Mock
    CategoryService categoryServiceMock;

    @InjectMocks
    RemoveElementCommand removeElementCommandTest;

    @Test
    public void getNameCommandTest() {
        assertEquals(NamesCommand.REMOVE_ELEMENT, removeElementCommandTest.getNameCommand());
    }

    @Test
    public void executeMoreThanTwoArguments(){
        String commandText = "/ a b";
                SendMessage sendMessage = removeElementCommandTest.execute(CHAT_ID, commandText);
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Неверный формат команды!" +
                        " Используйте: /removeElement <название элемента>",
                sendMessage.getParameters().get("text"));
    }

    @Test
    public void executeLessThanTwoArguments(){
        String commandText = "/";
        SendMessage sendMessage = removeElementCommandTest.execute(CHAT_ID, commandText);
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Неверный формат команды!" +
                        " Используйте: /removeElement <название элемента>",
                sendMessage.getParameters().get("text"));
    }

    @Test
    public void executeTwoArguments() {
        String commandText = "/removeElement a";
        SendMessage sendMessage = removeElementCommandTest.execute(CHAT_ID, commandText);
        verify(categoryServiceMock).removeCategory("a");
        assertEquals(CHAT_ID, sendMessage.getParameters().get("chat_id"));
        assertEquals("Элемент <<a>> удален.",
                sendMessage.getParameters().get("text"));
    }
}
