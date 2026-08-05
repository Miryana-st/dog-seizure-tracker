package app.model.dto.medication;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationScheduleRequest {

    @NotNull
    private UUID medicationId;

    @NotNull(message = "Administration time is required")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime administrationTime;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be a positive value")
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private BigDecimal amount;

    @NotNull(message = "Dosage is required")
    private Dosage dosage;
}
