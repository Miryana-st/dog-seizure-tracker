package app.service.medication.client;

import app.model.dto.medication.MedicationResponse;
import app.model.dto.medication.MedicationRequest;
import app.model.dto.medication.MedicationScheduleRequest;
import app.model.dto.medication.MedicationScheduleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "medication-svc", url = "http://localhost:8081/api/v1")
public interface MedicationClient {

    @PostMapping("/medications/{dogId}/new")
    ResponseEntity<Void> createMedication(@PathVariable("dogId") UUID dogId, @RequestHeader("X-User-Id") UUID userId, @RequestBody MedicationRequest requestBody);

    @GetMapping("/medications/{dogId}")
    List<MedicationResponse> getMedicationsByDogId(@PathVariable("dogId") UUID dogId);

    @GetMapping("/medications/{dogId}/{medicationId}/details")
    MedicationResponse getMedicationById(@PathVariable("dogId") UUID dogId, @PathVariable("medicationId") UUID medicationId);

    @PutMapping("/medications/{dogId}/{medicationId}/details")
    ResponseEntity<MedicationResponse> updateMedication(@PathVariable("dogId") UUID dogId, @PathVariable("medicationId") UUID medicationId, @RequestHeader("X-User-Id") UUID userId, @RequestBody MedicationRequest request);

    @DeleteMapping("/medications/{dogId}/{medicationId}")
    ResponseEntity<Void> deleteMedication(@PathVariable("dogId") UUID dogId, @PathVariable("medicationId") UUID medicationId, @RequestHeader("X-User-Id") UUID userId);

    @GetMapping("/medication-schedule/{dogId}")
    List<MedicationScheduleResponse> getMedicationScheduleByDogId(@PathVariable("dogId") UUID dogId);

    @PostMapping("/medication-schedule/{dogId}/new")
    ResponseEntity<Void> createMedicationSchedule(@PathVariable("dogId") UUID dogId, @RequestHeader("X-User-Id") UUID userId, @RequestBody MedicationScheduleRequest requestBody);

    @DeleteMapping("/medication-schedule/{dogId}/{medicationScheduleId}")
    ResponseEntity<Void> deleteMedicationSchedule(@PathVariable("dogId") UUID dogId, @PathVariable("medicationScheduleId") UUID medicationScheduleId, @RequestHeader("X-User-Id") UUID userId);

    @GetMapping("/medication-schedule/{dogId}/{medicationScheduleId}/details")
    ResponseEntity<MedicationScheduleResponse> getMedicationScheduleById(@PathVariable("dogId") UUID dogId, @PathVariable("medicationScheduleId") UUID medicationId);

    @PutMapping("/medication-schedule/{dogId}/{medicationScheduleId}/details")
    ResponseEntity<MedicationScheduleResponse> updateMedicationSchedule(@PathVariable("dogId") UUID dogId, @PathVariable("medicationScheduleId") UUID medicationScheduleId, @RequestHeader("X-User-Id") UUID userId, @RequestBody MedicationScheduleRequest request);
}
