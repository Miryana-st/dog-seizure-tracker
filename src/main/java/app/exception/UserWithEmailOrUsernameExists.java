package app.exception;

public class UserWithEmailOrUsernameExists extends RuntimeException {
    public UserWithEmailOrUsernameExists(String message) {
        super(message);
    }
}
