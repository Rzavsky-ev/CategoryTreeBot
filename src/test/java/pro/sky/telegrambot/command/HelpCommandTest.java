package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.service.CategoryService;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Тестовый класс для {@link HelpCommand}.
 * Проверяет отображение справочной информации о доступных командах.
 */
@ExtendWith(MockitoExtension.class)
public class HelpCommandTest {

    private final Long CHAT_ID = 123L;

    @Mock
    CategoryService categoryServiceMock;

    @InjectMocks
    HelpCommand helpCommandTest;

    @Test
    public void getNameCommandTest() {
        assertEquals(NamesCommand.HELP, helpCommandTest.getNameCommand());
    }

    @Test
    public void executeTest() {
        String commandText = "/help";
        String helpText = """
                =============================
                🏷️ *СПИСОК ДОСТУПНЫХ КОМАНД*
                =============================

                🔹 *Добавление элементов:*
                /addElement <название элемента>  - новая корневая категория
                /addElement <название родительского элемента>
                 <название дочернего элемента>  - вложенная категория

                🔹 *Просмотр дерева:*
                /viewTree - отобразить всё дерево

                🔹 *Удаление элемента:*
                /removeElement - удалить категорию

                🔹 *Скачивание Excel документа:*
                /download - скачать таблицу Excel

                🔹 *Считывание Excel документа:*
                /upload - считать таблицу Excel

                🔹 *Справка:*
                /help - список доступных команд
                =============================================================
                """;
        SendMessage sendMessage = helpCommandTest.execute(CHAT_ID, commandText);
        assertEquals(CHAT_ID.toString(), sendMessage.getParameters().get("chat_id"));
        assertEquals(helpText,
                sendMessage.getParameters().get("text"));
    }
}
