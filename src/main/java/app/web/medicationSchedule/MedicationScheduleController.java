package app.web.medicationSchedule;

import app.model.dto.medication.MedicationScheduleRequest;
import app.model.dto.medication.MedicationScheduleResponse;
import app.model.entity.dog.Dog;
import app.security.user.UserData;
import app.service.dog.DogService;
import app.service.medication.MedicationScheduleService;
import app.service.medication.MedicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.UUID;

@Controller
@RequestMapping("/medication-schedule")
public class MedicationScheduleController {

    private final DogService dogService;
    private final MedicationService medicationService;
    private final MedicationScheduleService medicationScheduleService;

    @Autowired
    public MedicationScheduleController(DogService dogService, MedicationService medicationService, MedicationScheduleService medicationScheduleService) {
        this.dogService = dogService;
        this.medicationService = medicationService;
        this.medicationScheduleService = medicationScheduleService;
    }

    @GetMapping
    public ModelAndView getMedicationScheduleSelectionPage(
            @AuthenticationPrincipal UserData userData) {

        ModelAndView modelAndView = new ModelAndView("medication-schedule");
        modelAndView.addObject("dogs", dogService.getAllDogsByOwnerId(userData.getUserId()));
        modelAndView.addObject("medicationSchedules", Collections.emptyList());

        return modelAndView;
    }

    @GetMapping("/{dogId}")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView getMedicationSchedulePage(
            @PathVariable UUID dogId,
            @AuthenticationPrincipal UserData userData) {

        ModelAndView modelAndView = new ModelAndView("medication-schedule");
        modelAndView.addObject("dogs", dogService.getAllDogsByOwnerId(userData.getUserId()));
        modelAndView.addObject("selectedDogId", dogId);
        modelAndView.addObject("dog", dogService.getDogById(dogId));
        modelAndView.addObject("dogAge", dogService.calculateDogAge(dogId));
        modelAndView.addObject("medicationSchedules", medicationScheduleService.getMedicationSchedulesByDogId(dogId));
        modelAndView.addObject("dueMedications", medicationScheduleService.getDueMedicationSchedules(dogId));
        return modelAndView;
    }

    @GetMapping("/{dogId}/new")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView getAddMedicationSchedulePage(
            @PathVariable UUID dogId) {

        Dog dog = dogService.getDogById(dogId);

        ModelAndView modelAndView = new ModelAndView("add-medication-schedule");
        modelAndView.addObject("dog", dog);
        modelAndView.addObject("medications", medicationService.getMedicationsByDogId(dogId));
        modelAndView.addObject("addMedicationScheduleRequest", new MedicationScheduleRequest());

        return modelAndView;
    }

    @PostMapping("/{dogId}/new")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView addMedicationSchedulePage(
            @Valid @ModelAttribute("addMedicationScheduleRequest") MedicationScheduleRequest addMedicationScheduleRequest,
            BindingResult bindingResult,
            @PathVariable UUID dogId) {

        Dog dog = dogService.getDogById(dogId);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("add-medication-schedule");

            modelAndView.addObject("dog", dog);
            modelAndView.addObject("medications", medicationService.getMedicationsByDogId(dogId));
            modelAndView.addObject("addMedicationScheduleRequest", addMedicationScheduleRequest);

            return modelAndView;
        }

        medicationScheduleService.addMedicationSchedule(
                dogId,
                addMedicationScheduleRequest.getMedicationId(),
                addMedicationScheduleRequest.getAdministrationTime(),
                addMedicationScheduleRequest.getAmount(),
                addMedicationScheduleRequest.getDosage());

        return new ModelAndView("redirect:/medication-schedule/" + dogId);
    }

    @GetMapping("/{dogId}/{medicationScheduleId}/details")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView getEditMedicationSchedulePage(
            @PathVariable UUID dogId,
            @PathVariable UUID medicationScheduleId) {

        MedicationScheduleResponse medicationSchedule = medicationScheduleService.getMedicationScheduleById(dogId, medicationScheduleId);

        MedicationScheduleRequest editMedicationScheduleRequest = MedicationScheduleRequest.builder()
                .medicationId(medicationSchedule.getMedicationId())
                .administrationTime(medicationSchedule.getAdministrationTime())
                .amount(medicationSchedule.getAmount())
                .dosage(medicationSchedule.getDosage())
                .build();

        ModelAndView modelAndView = new ModelAndView("medication-schedule-profile");

        modelAndView.addObject("medicationSchedule", medicationSchedule);
        modelAndView.addObject("editMedicationScheduleRequest", editMedicationScheduleRequest);
        modelAndView.addObject("medications", medicationService.getMedicationsByDogId(dogId));
        return modelAndView;
    }

    @PutMapping("/{dogId}/{medicationScheduleId}/details")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView updateMedicationSchedule(
            @Valid @ModelAttribute("editMedicationScheduleRequest") MedicationScheduleRequest editMedicationScheduleRequest,
            BindingResult bindingResult,
            @PathVariable UUID dogId,
            @PathVariable UUID medicationScheduleId) {

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("medication-schedule-profile");

            modelAndView.addObject("medicationSchedule", medicationScheduleService.getMedicationScheduleById(dogId, medicationScheduleId));
            modelAndView.addObject("medications", medicationService.getMedicationsByDogId(dogId));
            modelAndView.addObject("editMedicationScheduleRequest", editMedicationScheduleRequest);
            modelAndView.addObject("dog", dogService.getDogById(dogId));

            return modelAndView;
        }

        medicationScheduleService.updateMedicationSchedule(
                dogId,
                medicationScheduleId,
                editMedicationScheduleRequest.getMedicationId(),
                editMedicationScheduleRequest.getAdministrationTime(),
                editMedicationScheduleRequest.getAmount(),
                editMedicationScheduleRequest.getDosage());

        return new ModelAndView("redirect:/medication-schedule/" + dogId);
    }

    @DeleteMapping("/{dogId}/{medicationScheduleId}")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public String deleteMedicationSchedule(
            @PathVariable UUID dogId,
            @PathVariable UUID medicationScheduleId) {

        medicationScheduleService.deleteMedicationSchedule(dogId, medicationScheduleId);

        return "redirect:/medication-schedule/" + dogId;
    }
}
