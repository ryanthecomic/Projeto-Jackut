package exceptions;

public class CommunityNotFoundException extends RuntimeException {
    public CommunityNotFoundException(String message) {
        super(message);
    }
}
