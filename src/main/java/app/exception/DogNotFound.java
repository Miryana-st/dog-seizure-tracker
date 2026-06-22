package app.exception;

public class DogNotFound extends RuntimeException {
    public DogNotFound(String message) {
        super(message);
    }
}
