package pro.sky.telegrambot.exception;

/**
 * Исключение, которое выбрасывается, когда имя элемента требует кавычек, но они отсутствуют.
 */
public class ElementNameQuotesRequiredException extends RuntimeException {
    public ElementNameQuotesRequiredException(String message) {
        super(message);
    }
}
