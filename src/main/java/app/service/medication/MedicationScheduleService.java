package app.service.medication;

import app.exception.MedicationMicroserviceUnavailableException;
import app.exception.NotFoundException;
import app.model.dto.medication.Dosage;
import app.model.dto.medication.MedicationScheduleRequest;
import app.model.dto.medication.MedicationScheduleResponse;
import app.service.medication.client.MedicationClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    @Autowired
    public MedicationScheduleService(MedicationClient client) {
        this.client = client;
    }

    @Caching(evict = {
            @CacheEvict(value = "medicationSchedulesByDogId", allEntries = true),
            @CacheEvict(value = "medicationScheduleByDogAndId", allEntries = true),
            @CacheEvict(value = "dueMedicationSchedulesByDogId", allEntries = true)
    })
    public void addMedicationSchedule(UUID dogId, UUID medicationId, LocalTime administrationTime, BigDecimal amount, Dosage dosage) {

        MedicationScheduleRequest dto = MedicationScheduleRequest.builder()
                .medicationId(medicationId)
                .administrationTime(administrationTime)
                .amount(amount)
                .dosage(dosage)
                .build();

        try {
            client.createMedicationSchedule(dogId, dto);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            throw new MedicationMicroserviceUnavailableException(MEDICATION_MICROSERVICE_UNAVAILABLE);
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "medicationSchedulesByDogId", allEntries = true),
            @CacheEvict(value = "medicationScheduleByDogAndId", allEntries = true),
            @CacheEvict(value = "dueMedicationSchedulesByDogId", allEntries = true)
    })
    public void updateMedicationSchedule(UUID dogId, UUID medicationScheduleId, UUID medicationId, LocalTime administrationTime, BigDecimal amount, Dosage dosage) {

        MedicationScheduleRequest dto = MedicationScheduleRequest.builder()
                .medicationId(medicationId)
                .administrationTime(administrationTime)
                .amount(amount)
                .dosage(dosage)
                .build();

        try {
            client.updateMedicationSchedule(dogId, medicationScheduleId, dto);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            throw new NotFoundException(MEDICATION_SCHEDULE_NOT_FOUND);
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "medicationSchedulesByDogId", allEntries = true),
            @CacheEvict(value = "medicationScheduleByDogAndId", allEntries = true),
            @CacheEvict(value = "dueMedicationSchedulesByDogId", allEntries = true)
    })
    public void deleteMedicationSchedule(UUID dogId, UUID medicationScheduleId) {

        try {
            client.deleteMedicationSchedule(dogId, medicationScheduleId);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            throw new MedicationMicroserviceUnavailableException(MEDICATION_MICROSERVICE_UNAVAILABLE);
        }
    }

    @Cacheable(value = "medicationSchedulesByDogId", key = "#dogId")
    public List<MedicationScheduleResponse> getMedicationSchedulesByDogId(UUID dogId) {

        try {
            return client.getMedicationScheduleByDogId(dogId);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            throw new NotFoundException(MEDICATION_SCHEDULE_NOT_FOUND);
        }
    }

    @Cacheable(value = "medicationScheduleByDogAndId", key = "#dogId + ':' + #medicationScheduleId")
    public MedicationScheduleResponse getMedicationScheduleById(UUID dogId, UUID medicationScheduleId) {

        try {
            return client.getMedicationScheduleById(dogId, medicationScheduleId).getBody();
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            throw new NotFoundException(MEDICATION_SCHEDULE_NOT_FOUND);
        }
    }

    @Cacheable(value = "dueMedicationSchedulesByDogId", key = "#dogId")
    public List<MedicationScheduleResponse> getDueMedicationSchedules(UUID dogId) {

        try {
            return client.getDueMedicationSchedules(dogId).getBody();
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            throw new NotFoundException(MEDICATION_SCHEDULE_NOT_FOUND);
        }
    }
}
