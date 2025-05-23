package pro.sky.telegrambot.command;

import java.util.Arrays;
import java.util.Optional;


/**
 * Перечисление, содержащее все поддерживаемые команды бота и их описания.
 * <p>
 * Каждая команда содержит:
 * <ul>
 *     <li>Строковый идентификатор команды (например, "/addElement")</li>
 *     <li>Описание назначения команды</li>
 * </ul>
 * <p>
 * Также предоставляет методы для:
 * <ul>
 *     <li>Получения имени команды</li>
 *     <li>Получения описания команды</li>
 *     <li>Поиска команды по строковому идентификатору</li>
 * </ul>
 */
public enum NamesCommand {
    /**
     * Команда добавления категории в дерево
     */
    ADD_ELEMENT("/addElement", "Добавление категории"),

    /**
     * Команда удаления элемента из дерева категорий
     */
    REMOVE_ELEMENT("/removeElement", "Удаление элемента"),

    /**
     * Команда отображения всего дерева категорий
     */
    VIEW_TREE("/viewTree", "Отображение дерева"),

    /**
     * Команда скачивания дерева категорий в Excel-файле
     */
    DOWNLOAD("/download", "Скачивание Excel документа с деревом категорий"),

    /**
     * Команда загрузки дерева категорий из Excel-файла
     */
    UPLOAD("/upload", "Парсинг Excel документа с деревом категорий"),

    /**
     * Команда вызова справочной информации
     */
    HELP("/help", "Вызов справки");

    private final String nameCommand;
    private final String descriptionCommand;

    /**
     * Конструктор перечисления.
     *
     * @param nameCommand        строковый идентификатор команды
     * @param descriptionCommand описание назначения команды
     */
    NamesCommand(String nameCommand, String descriptionCommand) {
        this.nameCommand = nameCommand;
        this.descriptionCommand = descriptionCommand;
    }

    /**
     * Возвращает строковый идентификатор команды.
     *
     * @return имя команды (например, "/addElement")
     */
    public String getNameCommand() {
        return nameCommand;
    }

    /**
     * Возвращает описание назначения команды.
     *
     * @return описание команды
     */
    public String getDescriptionCommand() {
        return descriptionCommand;
    }

    /**
     * Находит команду по её строковому идентификатору.
     * <p>
     * Поиск выполняется без учета регистра.
     *
     * @param text строковый идентификатор для поиска
     * @return Optional с найденной командой или пустой, если команда не найдена
     */
    public static Optional<NamesCommand> fromString(String text) {
        return Arrays.stream(values())
                .filter(nc -> nc.nameCommand.equalsIgnoreCase(text))
                .findFirst();
    }
}