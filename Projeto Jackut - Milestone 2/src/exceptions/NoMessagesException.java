package exceptions;

public class NoMessagesException extends RuntimeException {
    public NoMessagesException(String message) {
        super(message);
    }
}