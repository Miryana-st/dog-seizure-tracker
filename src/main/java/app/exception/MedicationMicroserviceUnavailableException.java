package app.exception;

public class MedicationMicroserviceUnavailableException extends RuntimeException {
    public MedicationMicroserviceUnavailableException(String message) {
        super(message);
    }
}
