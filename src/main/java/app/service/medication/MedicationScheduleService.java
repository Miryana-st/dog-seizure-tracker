package app.service.medication;

import app.model.dto.medication.Dosage;
import app.model.dto.medication.MedicationScheduleRequest;
import app.model.dto.medication.MedicationScheduleResponse;
import app.service.medication.client.MedicationClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MedicationScheduleService {

    private final MedicationClient client;

    @Autowired
    public MedicationScheduleService(MedicationClient client) {
        this.client = client;
    }

    public void addMedicationSchedule(UUID dogId, UUID medicationId, LocalTime administrationTime, BigDecimal amount, Dosage dosage) {

        MedicationScheduleRequest dto = MedicationScheduleRequest.builder()
                .dogId(dogId)
                .medicationId(medicationId)
                .administrationTime(administrationTime)
                .amount(amount)
                .dosage(dosage)
                .build();

        try {
            client.createMedicationSchedule(dogId, dto);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
        }
    }

    public List<MedicationScheduleResponse> getMedicationSchedulesByDogId(UUID dogId) {
        return client.getMedicationScheduleByDogId(dogId);
    }


    public void deleteMedicationSchedule(UUID medicationScheduleId, UUID dogId) {
        try {
            client.deleteMedicationSchedule(dogId, medicationScheduleId);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
        }
    }

    public MedicationScheduleResponse getMedicationScheduleById(UUID dogId, UUID medicationScheduleId) {
        return client.getMedicationScheduleById(dogId, medicationScheduleId).getBody();
    }

    public void updateMedicationSchedule(UUID dogId, UUID medicationScheduleId, UUID medicationId, LocalTime administrationTime, BigDecimal amount, Dosage dosage) {

        MedicationScheduleRequest dto = MedicationScheduleRequest.builder()
                .dogId(dogId)
                .medicationId(medicationId)
                .administrationTime(administrationTime)
                .amount(amount)
                .dosage(dosage)
                .build();

        try {
            client.updateMedicationSchedule(dogId, medicationScheduleId, dto);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
        }
    }
}
