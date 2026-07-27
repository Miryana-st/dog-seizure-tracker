package app.service.medication;

import app.model.dto.medication.MedicationResponse;
import app.model.dto.medication.MedicationRequest;
import app.service.medication.client.MedicationClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MedicationService {

    private final MedicationClient client;

    @Autowired
    public MedicationService(MedicationClient client) {
        this.client = client;
    }

    public void addMedication(UUID dogId, String name, LocalDate startDate, LocalDate endDate, BigDecimal medicationConcentrationMg) {

        MedicationRequest dto = MedicationRequest.builder()
                .dogId(dogId)
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .medicationConcentrationMg(medicationConcentrationMg)
                .build();

        try {
            client.createMedication(dogId, dto);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
        }
    }

    public List<MedicationResponse> getMedicationsByDogId(UUID dogId) {
            return client.getMedicationsByDogId(dogId);
    }

    public void updateMedication(UUID medicationId, UUID dogId, String name, LocalDate startDate, LocalDate endDate, BigDecimal medicationConcentrationMg) {

        MedicationRequest dto = MedicationRequest.builder()
                .dogId(dogId)
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .medicationConcentrationMg(medicationConcentrationMg)
                .build();

        try {
            client.updateMedication(dogId, medicationId, dto);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
        }
    }

    public MedicationResponse getMedicationByIdAndDogId(UUID id, UUID dogId) {
        return client.getMedicationById(dogId, id);
    }

    public void deleteMedication(UUID medicationId, UUID dogId) {
        try {
            client.deleteMedication(dogId, medicationId);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
        }
    }
}
