package app.model.dto.medication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationResponse {

    private UUID id;

    private UUID dogId;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal medicationConcentrationMg;
}
