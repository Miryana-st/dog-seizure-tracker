package app.model.dto.seizure;

import app.model.entity.seizure.SeizureRecovery;
import app.model.entity.seizure.SeizureSeverity;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNewSeizureRequest {

    @NotBlank
    @DateTimeFormat(pattern = "MMMM d, yyyy")
    private LocalDate date;

    @NotBlank
    @DateTimeFormat(pattern = "hh:mm")
    private LocalTime time;

    @NotBlank
    @DateTimeFormat(pattern = "mm:ss")
    private LocalTime duration;

    private SeizureSeverity severity;

    private SeizureRecovery recovery;
}
