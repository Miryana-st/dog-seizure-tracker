package app.service.medication;

import app.exception.MedicationMicroserviceUnavailableException;
import app.exception.NotFoundException;
import app.model.dto.medication.Dosage;
import app.model.dto.medication.MedicationScheduleRequest;
import app.model.dto.medication.MedicationScheduleResponse;
import app.service.dog.DogService;
import app.service.medication.client.MedicationClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static app.exception.ExceptionMessages.MEDICATION_MICROSERVICE_UNAVAILABLE;
import static app.exception.ExceptionMessages.MEDICATION_SCHEDULE_NOT_FOUND;

@Slf4j
@Service
public class MedicationScheduleService {

    private final MedicationClient client;
    private final DogService dogService;

    @Autowired
    public MedicationScheduleService(MedicationClient client, DogService dogService) {
        this.client = client;
        this.dogService = dogService;
    }

    public void addMedicationSchedule(UUID dogId, UUID medicationId, LocalTime administrationTime, BigDecimal amount, Dosage dosage, UUID userId) {
        dogService.verifyOwnership(dogId, userId);

        MedicationScheduleRequest dto = MedicationScheduleRequest.builder()
                .dogId(dogId)
                .medicationId(medicationId)
                .administrationTime(administrationTime)
                .amount(amount)
                .dosage(dosage)
                .build();

        try {
            client.createMedicationSchedule(dogId, userId, dto);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            throw new MedicationMicroserviceUnavailableException(MEDICATION_MICROSERVICE_UNAVAILABLE);
        }
    }

    public List<MedicationScheduleResponse> getMedicationSchedulesByDogId(UUID dogId, UUID userId) {
        dogService.verifyOwnership(dogId, userId);
        try {
            return client.getMedicationScheduleByDogId(dogId);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            throw new NotFoundException(MEDICATION_SCHEDULE_NOT_FOUND);
        }
    }

    public void deleteMedicationSchedule(UUID dogId, UUID medicationScheduleId, UUID userId) {
        dogService.verifyOwnership(dogId, userId);
        try {
            client.deleteMedicationSchedule(dogId, medicationScheduleId, userId);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            throw new MedicationMicroserviceUnavailableException(MEDICATION_MICROSERVICE_UNAVAILABLE);
        }
    }

    public MedicationScheduleResponse getMedicationScheduleById(UUID dogId, UUID medicationScheduleId, UUID userId) {
        dogService.verifyOwnership(dogId, userId);
        try {
            return client.getMedicationScheduleById(dogId, medicationScheduleId).getBody();
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            throw new NotFoundException(MEDICATION_SCHEDULE_NOT_FOUND);
        }
    }

    public void updateMedicationSchedule(UUID dogId, UUID medicationScheduleId, UUID medicationId, LocalTime administrationTime, BigDecimal amount, Dosage dosage, UUID userId) {
        dogService.verifyOwnership(dogId, userId);

        MedicationScheduleRequest dto = MedicationScheduleRequest.builder()
                .dogId(dogId)
                .medicationId(medicationId)
                .administrationTime(administrationTime)
                .amount(amount)
                .dosage(dosage)
                .build();

        try {
            client.updateMedicationSchedule(dogId, medicationScheduleId, userId, dto);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            throw new NotFoundException(MEDICATION_SCHEDULE_NOT_FOUND);
        }
    }
}
