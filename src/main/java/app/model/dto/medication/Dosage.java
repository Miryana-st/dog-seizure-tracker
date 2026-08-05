package app.model.dto.medication;

public enum Dosage {
    TABLET("Tablet"),
    ML("Ml"),
    ;

    private final String displayDosage;

    Dosage(String displayDosage) {
        this.displayDosage = displayDosage;
    }
}
