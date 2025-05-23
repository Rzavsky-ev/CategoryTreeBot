package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.CommandService;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Сервис обработки входящих обновлений Telegram бота.
 * <p>
 * Реализует интерфейс UpdatesListener для получения и обработки обновлений от Telegram API.
 * Основные функции:
 * <ul>
 *     <li>Регистрация обработчика обновлений при инициализации</li>
 *     <li>Обработка каждого входящего обновления</li>
 *     <li>Делегирование обработки команд сервису CommandService</li>
 *     <li>Логирование входящих обновлений</li>
 * </ul>
 *
 * @see UpdatesListener Интерфейс слушателя обновлений Telegram
 * @see CommandService Сервис обработки команд
 */
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final CommandService commandService;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param telegramBot экземпляр Telegram бота
     * @param commandService сервис для обработки команд
     */
    public TelegramBotUpdatesListener(TelegramBot telegramBot, CommandService commandService) {
        this.telegramBot = telegramBot;
        this.commandService = commandService;
    }

    /**
     * Метод инициализации, регистрирующий текущий экземпляр как обработчик обновлений.
     * <p>
     * Вызывается автоматически после создания бина.
     */
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /**
     * Обрабатывает список входящих обновлений.
     * <p>
     * Для каждого обновления:
     * <ol>
     *     <li>Выполняет логирование обновления</li>
     *     <li>Если обновление не null, передает его на обработку в CommandService</li>
     * </ol>
     *
     * @param updates список входящих обновлений от Telegram API
     * @return константа CONFIRMED_UPDATES_ALL, подтверждающая обработку всех обновлений
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update != null) {
                commandService.processCommand(update);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}

