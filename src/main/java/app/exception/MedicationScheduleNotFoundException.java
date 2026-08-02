package app.exception;

public class MedicationScheduleNotFoundException extends RuntimeException {
    public MedicationScheduleNotFoundException(String message) {
        super(message);
    }
}
