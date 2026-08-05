package app.model.dto.medication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationRequest {

    @NotBlank(message = "*Medication name must not be empty.")
    private String name;

    @NotNull(message = "*Please enter a start date.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @Positive(message = "*Concentration must be a positive value.")
    @NotNull(message = "*Please enter a concentration.")
    private BigDecimal medicationConcentrationMg;
}
