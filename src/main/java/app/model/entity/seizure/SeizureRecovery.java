package app.model.entity.seizure;

import lombok.Getter;

@Getter
public enum SeizureRecovery {
    SLOW("Slow"),
    NORMAL("Normal"),
    FAST("Fast");

    private final String displayRecovery;

    SeizureRecovery(String displayRecovery) {
        this.displayRecovery = displayRecovery;
    }
}
