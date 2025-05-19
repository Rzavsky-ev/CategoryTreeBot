package pro.sky.telegrambot.exception;
/**
 * Исключение, выбрасываемое при попытке доступа к несуществующей категории.
 */
public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
