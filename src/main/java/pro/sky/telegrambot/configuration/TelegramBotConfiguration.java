package pro.sky.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Класс конфигурации для настройки Telegram бота.
 * <p>
 * Этот класс Spring конфигурации выполняет:
 * <ul>
 *     <li>Инициализацию экземпляра Telegram бота с использованием токена</li>
 *     <li>Очистку всех ранее установленных команд бота</li>
 * </ul>
 * <p>
 * Токен бота загружается из свойств приложения по ключу {@code telegram.bot.token}.
 *
 * @see TelegramBot Клиент Telegram Bot API
 * @see DeleteMyCommands Запрос на удаление команд бота
 */
@Configuration
public class TelegramBotConfiguration {

    /**
     * Токен Telegram бота, загружаемый из свойств приложения.
     */
    @Value("${telegram.bot.token}")
    private String token;

    /**
     * Создает и настраивает экземпляр Telegram бота.
     * <p>
     * При создании бота:
     * <ol>
     *     <li>Инициализирует бота с указанным токеном</li>
     *     <li>Удаляет все ранее установленные команды бота</li>
     * </ol>
     *
     * @return настроенный экземпляр Telegram бота
     */
    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }

}
