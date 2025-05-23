package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.exception.CategoryExistsException;
import pro.sky.telegrambot.exception.CategoryNotFoundException;
import pro.sky.telegrambot.service.CategoryService;

import java.util.List;

/**
 * Класс команды для добавления элементов (категорий) в иерархическую структуру.
 * Поддерживает добавление как корневых, так и дочерних категорий.
 * <p>
 * Форматы команд:
 * <ul>
 *     <li>Добавление корневой категории: {@code /addElement "Название элемента"}</li>
 *     <li>Добавление дочерней категории: {@code /addElement "Родительский элемент" "Дочерний элемент"}</li>
 * </ul>
 * <p>
 * В случае ошибок (неправильный формат команды, дублирование категорий и т.д.)
 * пользователю отправляется соответствующее сообщение об ошибке.
 *
 * @see Command Базовый интерфейс команд
 * @see CategoryService Сервис для работы с категориями
 */
@Component
public class AddElementCommand implements Command {

    private final CategoryService categoryService;

    /**
     * Конструктор с внедрением зависимости CategoryService.
     *
     * @param categoryService сервис для работы с категориями
     */
    public AddElementCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Возвращает имя команды, которое она обрабатывает.
     *
     * @return имя команды (ADD_ELEMENT)
     */
    @Override
    public NamesCommand getNameCommand() {
        return NamesCommand.ADD_ELEMENT;
    }

    /**
     * Выполняет логику команды по добавлению элемента (категории).
     * <p>
     * В зависимости от количества аргументов:
     * <ul>
     *     <li>2 аргумента - добавляет корневую категорию</li>
     *     <li>3 аргумента - добавляет дочернюю категорию к указанному родителю</li>
     * </ul>
     *
     * @param chatId    идентификатор чата для отправки ответа
     * @param arguments список аргументов команды
     * @return SendMessage с результатом выполнения операции или сообщением об ошибке
     */
    @Override
    public SendMessage execute(Long chatId, List<String> arguments) {
        if (arguments.size() < 2 || arguments.size() > 3) {
            return new SendMessage(chatId, """
                        ⚠ *Ошибка формата команды!*
                        Правильное использование:
                     **Добавить корневую категорию:**
                       /addElement "Название элемента"
                     **Добавить дочернюю категорию:**
                       /addElement "Родительский элемент" "Дочерний элемент"
                    ❗ *Не забудьте кавычки!* ❗""");
        }
        try {
            if (arguments.size() == 2) {
                categoryService.addRootCategory(arguments.get(1));
                return new SendMessage(chatId,
                        "Корневой элемент \"" + arguments.get(1) + "\" добавлен.");
            } else {
                categoryService.addChildCategory(arguments.get(1), arguments.get(2));
                return new SendMessage(chatId,
                        "Дочерний элемент \"" + arguments.get(2) + "\" добавлен.");
            }
        } catch (CategoryExistsException | CategoryNotFoundException e) {
            return new SendMessage(chatId, "Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return new SendMessage(chatId,
                    "Произошла непредвиденная ошибка при добавлении элемента.");
        }
    }
}
