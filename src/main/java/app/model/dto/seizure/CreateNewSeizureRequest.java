package app.model.dto.seizure;

import app.model.entity.seizure.SeizureRecovery;
import app.model.entity.seizure.SeizureSeverity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNewSeizureRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

    @Positive(message = "*Duration must be a positive number")
    private int duration;

    private String note;

    private boolean cluster;

    @NotNull(message = "*Severity cannot be empty")
    private SeizureSeverity severity;

    @NotNull(message = "*Recovery cannot be empty")
    private SeizureRecovery recovery;
}
