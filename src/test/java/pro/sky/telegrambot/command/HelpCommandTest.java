package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-тесты для {@link HelpCommand}, проверяющие корректность работы команды помощи.
 * <p>
 * Тесты покрывают следующие сценарии:
 * <ul>
 *   <li>Получение имени команды</li>
 *   <li>Обработку команды с корректным количеством аргументов (1 аргумент)</li>
 *   <li>Обработку команды с некорректным количеством аргументов (2 аргумента)</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class HelpCommandTest {

    /**
     * Тестовый идентификатор чата для проверки отправки сообщений.
     */
    private final Long CHAT_ID = 123L;

    @InjectMocks
    HelpCommand helpCommandTest;

    /**
     * Проверяет корректность возвращаемого имени команды.
     * <p>
     * Ожидаемый результат: имя команды должно соответствовать {@link NamesCommand#HELP}.
     */
    @Test
    public void getNameCommandTest() {
        assertEquals(NamesCommand.HELP, helpCommandTest.getNameCommand());
    }

    /**
     * Тестирует обработку команды с одним аргументом (валидный случай).
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Корректность установки chat_id в ответном сообщении</li>
     *   <li>Полноту и формат выводимой справки</li>
     * </ul>
     */
    @Test
    public void executeOneArgument() {
        String commandText = "/help";
        String helpText = """
                =============================
                🏷️ *СПИСОК ДОСТУПНЫХ КОМАНД*
                =============================
                
                🔹 *Добавление элементов:*
                /addElement "название элемента" - новая корневая категория
                /addElement "родительский элемент" "дочерний элемент" - вложенная категория
                (кавычки обязательны в названиях!)
                
                🔹 *Просмотр дерева:*
                /viewTree - отобразить всё дерево
                
                🔹 *Удаление элемента:*
                /removeElement "название элемента" - удалить категорию
                (не забудьте кавычки!)
                
                🔹 *Скачивание/загрузка:*
                /download - скачать таблицу Excel
                /upload - загрузить таблицу Excel
                
                🔹 *Справка:*
                /help - список доступных команд
                =============================""";
        SendMessage sendMessage = helpCommandTest.execute(CHAT_ID, List.of(commandText));
        assertEquals(CHAT_ID.toString(), sendMessage.getParameters().get("chat_id"));
        assertEquals(helpText,
                sendMessage.getParameters().get("text"));
    }

    /**
     * Тестирует обработку команды с двумя аргументами (невалидный случай).
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Корректность установки chat_id в ответном сообщении</li>
     *   <li>Вывод сообщения об ошибке формата команды</li>
     * </ul>
     */
    @Test
    public void executeTwoArguments() {
        String commandText1 = "/help";
        String commandText2 = "a";
        String helpText = "Неверный формат команды! Используйте: /help";
        SendMessage sendMessage = helpCommandTest.execute(CHAT_ID, List.of(commandText1, commandText2));
        assertEquals(CHAT_ID.toString(), sendMessage.getParameters().get("chat_id"));
        assertEquals(helpText,
                sendMessage.getParameters().get("text"));
    }
}
