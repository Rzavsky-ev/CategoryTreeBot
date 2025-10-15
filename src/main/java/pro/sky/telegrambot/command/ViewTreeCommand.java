package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.exception.CategoryTreeIsEmptyException;
import pro.sky.telegrambot.service.CategoryService;

import java.util.List;

/**
 * Команда для отображения иерархического дерева категорий.
 */
@Component
public class ViewTreeCommand implements Command {

    private final CategoryService categoryService;

    public ViewTreeCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Возвращает имя команды VIEW_TREE.
     *
     * @return имя команды (VIEW_TREE)
     */
    @Override
    public NamesCommand getNameCommand() {
        return NamesCommand.VIEW_TREE;
    }

    /**
     * Выполняет команду отображения дерева категорий.
     *
     * @param chatId    идентификатор чата для отправки сообщения
     * @param arguments аргументы команды
     * @return SendMessage с деревом категорий или сообщением об ошибке
     */
    @Override
    public SendMessage execute(Long chatId, List<String> arguments) {
        if (arguments.size() > 1) {
            return new SendMessage(chatId,
                    "Неверный формат команды! Используйте: /viewTree");
        }
        try {
            return new SendMessage(chatId,
                    categoryService.viewTree());
        } catch (CategoryTreeIsEmptyException e) {
            return new SendMessage(chatId, "Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return new SendMessage(chatId,
                    "Произошла непредвиденная ошибка при формировании дерева категорий.");
        }
    }
}
