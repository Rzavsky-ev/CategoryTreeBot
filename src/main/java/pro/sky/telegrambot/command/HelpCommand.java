package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

/**
 * Обработчик команды /help.
 * Предоставляет список всех доступных команд бота с описанием.
 */
@Component
public class HelpCommand implements Command {

    private final String helpText = """
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

    @Override
    public NamesCommand getNameCommand() {
        return NamesCommand.HELP;
    }

    @Override
    public SendMessage execute(Long chatId, String commandText) {
        return new SendMessage(chatId.toString(), helpText);
    }
}
