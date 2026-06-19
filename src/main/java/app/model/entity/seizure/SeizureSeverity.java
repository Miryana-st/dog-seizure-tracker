package app.model.entity.seizure;

import lombok.Getter;

@Getter
public enum SeizureSeverity {
    MILD ("Mild"),
    MODERATE ("Moderate"),
    SEVERE ("Severe"),
    ;

    private final String displaySeverity;

    SeizureSeverity(String displaySeverity) {
        this.displaySeverity = displaySeverity;
    }
}
