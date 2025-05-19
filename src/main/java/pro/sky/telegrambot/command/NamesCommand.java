package pro.sky.telegrambot.command;

import java.util.Arrays;
import java.util.Optional;

/**
 * Перечисление команд Telegram-бота с их текстовыми представлениями и описаниями.
 * Каждая константа представляет одну команду бота.
 */
public enum NamesCommand {
    ADD_ELEMENT("/addElement", "Добавление категории"),
    REMOVE_ELEMENT("/removeElement", "Удаление элемента"),
    VIEW_TREE("/viewTree", "Отображение дерева"),
    DOWNLOAD("/download", "Скачивание Excel документа с деревом категорий"),
    UPLOAD("/upload", "Парсинг Excel документа с деревом категорий"),
    HELP("/help", "Вызов справки");

    private final String nameCommand;
    private final String descriptionCommand;

    NamesCommand(String nameCommand, String descriptionCommand) {
        this.nameCommand = nameCommand;
        this.descriptionCommand = descriptionCommand;
    }

    public String getNameCommand() {
        return nameCommand;
    }

    public String getDescriptionCommand() {
        return descriptionCommand;
    }

    public static Optional<NamesCommand> fromString(String text) {
        return Arrays.stream(values())
                .filter(nc -> nc.nameCommand.equalsIgnoreCase(text))
                .findFirst();
    }
}
