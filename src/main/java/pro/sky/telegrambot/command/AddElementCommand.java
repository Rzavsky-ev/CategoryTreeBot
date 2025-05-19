package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.exception.CategoryExistsException;
import pro.sky.telegrambot.exception.CategoryNotFoundException;
import pro.sky.telegrambot.service.CategoryService;

/**
 * Обработчик команды /addElement для добавления категорий в дерево.
 * Поддерживает два формата:
 * 1. Добавление корневой категории: /addElement <имя корневой категории>
 * 2. Добавление дочерней категории: /addElement <имя родительской категории>
 * <имя дочерней категории>
 */
@Component
public class AddElementCommand implements Command {

    private final CategoryService categoryService;

    public AddElementCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public NamesCommand getNameCommand() {
        return NamesCommand.ADD_ELEMENT;
    }

    @Override
    public SendMessage execute(Long chatId, String commandText) {
        String[] arguments = commandText.trim().split("\\s+");
        if (arguments.length < 2 || arguments.length > 3) {
            return new SendMessage(chatId,
                    "Неверный формат команды! Используйте: /addElement <имя родительского элемента>" +
                            " <имя дочернего элемента>" +
                            " или /addElement <имя корневого элемента>");
        }
        try {
            if (arguments.length == 2) {
                categoryService.addRootCategory(arguments[1]);
                return new SendMessage(chatId,
                        "Корневой элемент <<" + arguments[1] + ">> добавлен.");
            } else {
                categoryService.addChildCategory(arguments[1], arguments[2]);
                return new SendMessage(chatId,
                        "Дочерний элемент <<" + arguments[2] + ">> добавлен.");
            }
        } catch (CategoryExistsException | CategoryNotFoundException e) {
            return new SendMessage(chatId, "Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return new SendMessage(chatId,
                    "Произошла непредвиденная ошибка при добавлении элемента.");
        }
    }
}
