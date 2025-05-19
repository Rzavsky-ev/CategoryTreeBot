package pro.sky.telegrambot.exception;
/**
 * Исключение, возникающее при попытке работы с пустым деревом категорий.
 */
public class CategoryTreeIsEmptyException extends RuntimeException {
    public CategoryTreeIsEmptyException(String message) {
        super(message);
    }
}
