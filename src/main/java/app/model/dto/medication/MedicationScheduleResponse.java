package app.model.dto.medication;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class MedicationScheduleResponse {

    private UUID id;

    private String medicationName;

    private UUID dogId;

    private UUID medicationId;

    private LocalTime administrationTime;

    private BigDecimal amount;

    private Dosage dosage;
}
