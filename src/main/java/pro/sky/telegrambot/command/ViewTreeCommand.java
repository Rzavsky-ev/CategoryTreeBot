package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.exception.CategoryTreeIsEmptyException;
import pro.sky.telegrambot.service.CategoryService;

import java.util.List;

/**
 * Команда для отображения иерархического дерева категорий.
 * <p>
 * Предоставляет пользователю текстовое представление всего дерева категорий,
 * включая родительские и дочерние элементы с отступами для визуализации структуры.
 * <p>
 * Формат команды: {@code /viewTree}
 * <p>
 *
 * @see Command Базовый интерфейс команд
 * @see CategoryService Сервис для работы с категориями
 * @see CategoryTreeIsEmptyException Исключение при пустом дереве категорий
 */
@Component
public class ViewTreeCommand implements Command {

    private final CategoryService categoryService;

    /**
     * Конструктор с внедрением зависимости CategoryService.
     *
     * @param categoryService сервис для работы с категориями
     */
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
     * <p>
     * Проверяет корректность формата команды и возвращает:
     * <ul>
     *     <li>Форматированное дерево категорий (в случае успеха)</li>
     *     <li>Сообщение об ошибке формата команды</li>
     *     <li>Сообщение о пустом дереве категорий</li>
     *     <li>Сообщение о непредвиденной ошибке</li>
     * </ul>
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
