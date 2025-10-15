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
 */
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final CommandService commandService;

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, CommandService commandService) {
        this.telegramBot = telegramBot;
        this.commandService = commandService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /**
     * Обрабатывает список входящих обновлений.
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

