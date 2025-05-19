package pro.sky.telegrambot.exception;
/**
 * Исключение, выбрасываемое при обнаружении неверного формата Excel-файла.
 */
public class InvalidExcelFormatException extends RuntimeException {
    public InvalidExcelFormatException(String message) {
        super(message);
    }
}
