package pro.sky.telegrambot.exception;
/**
 * Исключение, возникающее при ошибке возникшей при отправке Excel документа.
 */
public class ErrorSendingFileException extends RuntimeException {
    public ErrorSendingFileException(String message) {
        super(message);
    }
}
