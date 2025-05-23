package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Команда для отображения справки по доступным командам бота.
 * <p>
 * Предоставляет пользователю список всех доступных команд с их описанием
 * и примерами использования. Команда не требует аргументов.
 * <p>
 * Формат команды: {@code /help}
 *
 * @see Command Базовый интерфейс команд
 * @see NamesCommand Перечисление доступных команд
 */
@Component
public class HelpCommand implements Command {

    /**
     * Текст справки с форматированием Markdown.
     * Содержит:
     * <ul>
     *     <li>Группировку команд по категориям</li>
     *     <li>Примеры использования</li>
     *     <li>Важные примечания (обязательные кавычки и т.д.)</li>
     * </ul>
     */
    private final String helpText = """
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

    /**
     * Возвращает имя команды HELP.
     *
     * @return имя команды (HELP)
     */
    @Override
    public NamesCommand getNameCommand() {
        return NamesCommand.HELP;
    }

    /**
     * Выполняет команду отображения справки.
     * <p>
     * Проверяет наличие лишних аргументов и возвращает
     * форматированное сообщение со списком команд.
     *
     * @param chatId    идентификатор чата для отправки сообщения
     * @param arguments аргументы команды
     * @return SendMessage с текстом справки или сообщением об ошибке формата
     */
    @Override
    public SendMessage execute(Long chatId, List<String> arguments) {
        if (arguments.size() > 1) {
            return new SendMessage(chatId.toString(),
                    "Неверный формат команды! Используйте: /help");
        }
        return new SendMessage(chatId.toString(), helpText);
    }
}
