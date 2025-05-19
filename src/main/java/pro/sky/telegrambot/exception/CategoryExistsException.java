package pro.sky.telegrambot.exception;
/**
 * Исключение, возникающее при попытке создать категорию, которая уже существует.
 */
public class CategoryExistsException extends RuntimeException {
  public CategoryExistsException(String message) {
    super(message);
  }
}
