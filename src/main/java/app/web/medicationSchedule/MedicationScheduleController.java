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
    public ModelAndView getMedicationSchedulePage(
            @PathVariable UUID dogId,
            @AuthenticationPrincipal UserData userData) {

        ModelAndView modelAndView = new ModelAndView("medication-schedule");
        modelAndView.addObject("dogs", dogService.getAllDogsByOwnerId(userData.getUserId()));
        modelAndView.addObject("selectedDogId", dogId);
        modelAndView.addObject("dog", dogService.getDogById(dogId));
        modelAndView.addObject("medicationSchedules", medicationScheduleService.getMedicationSchedulesByDogId(dogId, userData.getUserId()));

        return modelAndView;
    }

    @GetMapping("/{dogId}/new")
    public ModelAndView getAddMedicationSchedulePage(
            @PathVariable UUID dogId,
            @AuthenticationPrincipal UserData userData) {

        Dog dog = dogService.getDogById(dogId);

        ModelAndView modelAndView = new ModelAndView("add-medication-schedule");
        modelAndView.addObject("dog", dog);
        modelAndView.addObject("medications", medicationService.getMedicationsByDogId(dogId, userData.getUserId()));
        modelAndView.addObject("addMedicationScheduleRequest", new MedicationScheduleRequest());

        return modelAndView;
    }

    @PostMapping("/{dogId}/new")
    public ModelAndView addMedicationSchedulePage(
            @Valid @ModelAttribute("addMedicationScheduleRequest") MedicationScheduleRequest addMedicationScheduleRequest,
            BindingResult bindingResult,
            @PathVariable UUID dogId,
            @AuthenticationPrincipal UserData userData) {

        Dog dog = dogService.getDogById(dogId);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("add-medication-schedule");

            modelAndView.addObject("dog", dog);
            modelAndView.addObject("medications", medicationService.getMedicationsByDogId(dogId, userData.getUserId()));
            modelAndView.addObject("addMedicationScheduleRequest", addMedicationScheduleRequest);

            return modelAndView;
        }

        addMedicationScheduleRequest.setDogId(dogId);

        medicationScheduleService.addMedicationSchedule(addMedicationScheduleRequest.getDogId(), addMedicationScheduleRequest.getMedicationId(), addMedicationScheduleRequest.getAdministrationTime(), addMedicationScheduleRequest.getAmount(), addMedicationScheduleRequest.getDosage(), userData.getUserId());

        return new ModelAndView("redirect:/medication-schedule/" + dogId);
    }

    @GetMapping("/{dogId}/{medicationScheduleId}/details")
    public ModelAndView getEditMedicationSchedulePage(
            @PathVariable UUID dogId,
            @PathVariable UUID medicationScheduleId,
            @AuthenticationPrincipal UserData userData) {

        MedicationScheduleResponse medicationSchedule = medicationScheduleService.getMedicationScheduleById(dogId, medicationScheduleId, userData.getUserId());

        MedicationScheduleRequest editMedicationScheduleRequest = MedicationScheduleRequest.builder()
                .dogId(medicationSchedule.getDogId())
                .medicationId(medicationSchedule.getMedicationId())
                .administrationTime(medicationSchedule.getAdministrationTime())
                .amount(medicationSchedule.getAmount())
                .dosage(medicationSchedule.getDosage())
                .build();

        ModelAndView modelAndView = new ModelAndView("medication-schedule-profile");

        modelAndView.addObject("medicationSchedule", medicationSchedule);
        modelAndView.addObject("editMedicationScheduleRequest", editMedicationScheduleRequest);
        modelAndView.addObject("medications", medicationService.getMedicationsByDogId(dogId, userData.getUserId()));
        return modelAndView;
    }

    @PutMapping("/{dogId}/{medicationScheduleId}/details")
    public ModelAndView updateMedicationSchedule(
            @Valid @ModelAttribute("editMedicationScheduleRequest") MedicationScheduleRequest editMedicationScheduleRequest,
            BindingResult bindingResult,
            @PathVariable UUID dogId,
            @PathVariable UUID medicationScheduleId,
            @AuthenticationPrincipal UserData userData) {

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("medication-schedule-profile");

            modelAndView.addObject("medicationSchedule", medicationScheduleService.getMedicationScheduleById(dogId, medicationScheduleId, userData.getUserId()));
            modelAndView.addObject("medications", medicationService.getMedicationsByDogId(dogId, userData.getUserId()));
            modelAndView.addObject("editMedicationScheduleRequest", editMedicationScheduleRequest);
            modelAndView.addObject("dog", dogService.getDogById(editMedicationScheduleRequest.getDogId()));

            return modelAndView;
        }

        medicationScheduleService.updateMedicationSchedule(
                dogId,
                medicationScheduleId,
                editMedicationScheduleRequest.getMedicationId(),
                editMedicationScheduleRequest.getAdministrationTime(),
                editMedicationScheduleRequest.getAmount(),
                editMedicationScheduleRequest.getDosage(),
                userData.getUserId());

        return new ModelAndView("redirect:/medication-schedule/" + dogId);
    }


    @DeleteMapping("/{dogId}/{medicationScheduleId}")
    public String deleteMedicationSchedule(
            @PathVariable UUID dogId,
            @PathVariable UUID medicationScheduleId,
            @AuthenticationPrincipal UserData userData) {

        medicationScheduleService.deleteMedicationSchedule(dogId, medicationScheduleId, userData.getUserId());

        return "redirect:/medication-schedule/" + dogId;
    }
}
