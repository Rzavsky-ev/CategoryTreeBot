package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.model.Update;

/**
 * Сервис обработки команд Telegram бота.
 */
public interface CommandService {

    /**
     * Обрабатывает входящую команду из обновления Telegram.
     *
     * @param update входящее обновление от Telegram API, содержащее команду
     */
    void processCommand(Update update);
}
