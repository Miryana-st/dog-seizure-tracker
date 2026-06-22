package app.exception;

public class UserIncorrectPasswordOrUsername extends RuntimeException {
    public UserIncorrectPasswordOrUsername(String message) {
        super(message);
    }
}
