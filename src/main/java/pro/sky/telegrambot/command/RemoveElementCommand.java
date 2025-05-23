package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.exception.CategoryNotFoundException;
import pro.sky.telegrambot.service.CategoryService;

import java.util.List;

/**
 * Команда для удаления категории из иерархического дерева.
 * <p>
 * Позволяет удалить существующую категорию по её названию.
 * При удалении родительской категории также удаляются все её дочерние элементы.
 * <p>
 * Формат команды: {@code /removeElement "название элемента"}
 * <p>
 *
 * @see Command Базовый интерфейс команд
 * @see CategoryService Сервис для работы с категориями
 * @see CategoryNotFoundException Исключение при отсутствии категории
 */
@Component
public class RemoveElementCommand implements Command {

    private final CategoryService categoryService;

    /**
     * Конструктор с внедрением зависимости CategoryService.
     *
     * @param categoryService сервис для работы с категориями
     */
    public RemoveElementCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Возвращает имя команды REMOVE_ELEMENT.
     *
     * @return имя команды (REMOVE_ELEMENT)
     */
    @Override
    public NamesCommand getNameCommand() {
        return NamesCommand.REMOVE_ELEMENT;
    }

    /**
     * Выполняет удаление указанной категории.
     * <p>
     * Проверяет корректность формата команды и пытается удалить категорию.
     * В случае успеха возвращает подтверждение, при ошибках - соответствующее сообщение.
     *
     * @param chatId идентификатор чата для отправки ответа
     * @param arguments список аргументов команды
     * @return SendMessage с результатом выполнения операции:
     *         - подтверждение успешного удаления
     *         - сообщение об ошибке формата
     *         - сообщение о ненайденной категории
     *         - сообщение о непредвиденной ошибке
     */
    @Override
    public SendMessage execute(Long chatId, List<String> arguments) {
        if (arguments.size() != 2) {
            return new SendMessage(chatId,
                    "Неверный формат команды!" +
                            " Используйте: /removeElement \"название элемента\"");
        }
        try {
            categoryService.removeCategory(arguments.get(1));
            return new SendMessage(chatId,
                    "Элемент \"" + arguments.get(1) + "\" удален.");
        } catch (CategoryNotFoundException e) {
            return new SendMessage(chatId, "Ошибка: " + e.getMessage());
        } catch (
                Exception e) {
            return new SendMessage(chatId,
                    "Произошла непредвиденная ошибка при удалении элемента.");
        }
    }
}

