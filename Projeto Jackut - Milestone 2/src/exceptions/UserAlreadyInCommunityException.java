package exceptions;

public class UserAlreadyInCommunityException extends Exception {
    public UserAlreadyInCommunityException(String message) {
        super(message);
    }
}