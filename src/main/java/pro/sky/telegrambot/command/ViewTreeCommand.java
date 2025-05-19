package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.exception.CategoryTreeIsEmptyException;
import pro.sky.telegrambot.service.CategoryService;

/**
 * Обработчик команды /viewTree для отображения дерева.
 * Возвращает древовидную структуру в текстовом формате.
 */
@Component
public class ViewTreeCommand implements Command {

    private final CategoryService categoryService;

    public ViewTreeCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public NamesCommand getNameCommand() {
        return NamesCommand.VIEW_TREE;
    }

    @Override
    public SendMessage execute(Long chatId, String commandText) {
        String[] arguments = commandText.trim().split("\\s+");
        if (arguments.length > 1) {
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
