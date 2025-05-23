package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.request.SendMessage;

import java.util.List;

/**
 * Интерфейс, определяющий контракт для команд Telegram бота.
 * <p>
 * Каждая команда должна реализовывать этот интерфейс, предоставляя:
 * <ul>
 *     <li>Логику выполнения команды</li>
 *     <li>Свое уникальное имя</li>
 * </ul>
 *
 * @see SendMessage Объект для отправки сообщений через Telegram Bot API
 */
public interface Command {

    /**
     * Выполняет основную логику команды.
     *
     * @param chatId    уникальный идентификатор чата, в который нужно отправить ответ
     * @param arguments список аргументов команды
     * @return объект SendMessage с ответом пользователю
     */
    SendMessage execute(Long chatId, List<String> arguments);

    /**
     * Возвращает имя команды, которое используется для идентификации
     * и вызова соответствующей реализации.
     *
     * @return элемент перечисления NamesCommand, соответствующий данной команде
     * @see NamesCommand
     */
    NamesCommand getNameCommand();
}
