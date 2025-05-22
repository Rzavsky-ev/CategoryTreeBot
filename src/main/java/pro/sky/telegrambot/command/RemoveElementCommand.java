package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.exception.CategoryNotFoundException;
import pro.sky.telegrambot.service.CategoryService;

/**
 * Обработчик команды /removeElement для удаления категорий.
 * Удаляет категорию и все её дочерние элементы (каскадное удаление).
 */
@Component
public class RemoveElementCommand implements Command {

    private final CategoryService categoryService;

    public RemoveElementCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public NamesCommand getNameCommand() {
        return NamesCommand.REMOVE_ELEMENT;
    }

    @Override
    public SendMessage execute(Long chatId, String commandText) {
        String[] arguments = commandText.trim().split("\\s+");
        if (arguments.length != 2) {
            return new SendMessage(chatId,
                    "Неверный формат команды!" +
                            " Используйте: /removeElement <название элемента>");
        }
        try {
            categoryService.removeCategory(arguments[1]);
            return new SendMessage(chatId,
                    "Элемент <<" + arguments[1] + ">> удален.");
        } catch (CategoryNotFoundException e) {
            return new SendMessage(chatId, "Ошибка: " + e.getMessage());
        } catch (
                Exception e) {
            return new SendMessage(chatId,
                    "Произошла непредвиденная ошибка при удалении элемента.");
        }
    }
}

