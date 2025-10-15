package pro.sky.telegrambot.command;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;


/**
 * Перечисление, содержащее все поддерживаемые команды бота и их описания.
 */
@Getter
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

    /**
     * Находит команду по её строковому идентификатору.
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