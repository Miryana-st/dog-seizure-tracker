package app.web.medication;

import app.model.dto.medication.MedicationRequest;
import app.model.dto.medication.MedicationResponse;
import app.model.entity.dog.Dog;
import app.security.user.UserData;
import app.service.dog.DogService;
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
@RequestMapping("/medications")
public class MedicationController {

    private final DogService dogService;
    private final MedicationService medicationService;

    @Autowired
    public MedicationController(DogService dogService, MedicationService medicationService) {
        this.dogService = dogService;
        this.medicationService = medicationService;
    }

    @GetMapping
    public ModelAndView getMedicationSelectionPage(
            @AuthenticationPrincipal UserData userData) {

        ModelAndView modelAndView = new ModelAndView("medication");
        modelAndView.addObject("dogs", dogService.getAllDogsByOwnerId(userData.getUserId()));
        modelAndView.addObject("dogMedications", Collections.emptyList());

        return modelAndView;
    }

    @GetMapping("/{dogId}")
    public ModelAndView getMedicationsPage(
            @PathVariable UUID dogId,
            @AuthenticationPrincipal UserData userData) {

        ModelAndView modelAndView = new ModelAndView("medication");
        modelAndView.addObject("dogs", dogService.getAllDogsByOwnerId(userData.getUserId()));
        modelAndView.addObject("selectedDogId", dogId);
        modelAndView.addObject("dog", dogService.getDogById(dogId));
        modelAndView.addObject("dogMedications", medicationService.getMedicationsByDogId(dogId, userData.getUserId()));

        return modelAndView;
    }

    @GetMapping("/{dogId}/new")
    public ModelAndView getAddMedicationPage(
            @PathVariable UUID dogId,
            @AuthenticationPrincipal UserData userData) {

        Dog dog = dogService.getDogById(dogId);

        ModelAndView modelAndView = new ModelAndView("add-medication");

        modelAndView.addObject("dog", dog);
        modelAndView.addObject("addMedicationRequest", new MedicationRequest());

        return modelAndView;
    }

    @PostMapping("/{dogId}/new")
    public ModelAndView addMedicationPage(
            @Valid @ModelAttribute("addMedicationRequest") MedicationRequest addMedicationRequest,
            BindingResult bindingResult,
            @PathVariable UUID dogId,
            @AuthenticationPrincipal UserData userData) {

        Dog dog = dogService.getDogById(dogId);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("add-medication");

            modelAndView.addObject("dog", dog);
            modelAndView.addObject("addMedicationRequest", addMedicationRequest);

            return modelAndView;
        }

        addMedicationRequest.setDogId(dogId);

        medicationService.addMedication(addMedicationRequest.getDogId(), addMedicationRequest.getName(),
                addMedicationRequest.getStartDate(), addMedicationRequest.getEndDate(), addMedicationRequest.getMedicationConcentrationMg(), userData.getUserId());

        return new ModelAndView("redirect:/medications/" + dogId);
    }

    @GetMapping("/{dogId}/{medicationId}/details")
    public ModelAndView getEditMedicationPage(
            @PathVariable UUID dogId,
            @PathVariable UUID medicationId,
            @AuthenticationPrincipal UserData userData) {

        MedicationResponse medication = medicationService.getMedicationByIdAndDogId(medicationId, dogId, userData.getUserId());

        Dog dog = dogService.getDogById(medication.getDogId());

        MedicationRequest editMedicationRequest = MedicationRequest.builder()
                .dogId(medication.getDogId())
                .name(medication.getName())
                .startDate(medication.getStartDate())
                .endDate(medication.getEndDate())
                .medicationConcentrationMg(medication.getMedicationConcentrationMg())
                .build();

        ModelAndView modelAndView = new ModelAndView("medication-profile");

        modelAndView.addObject("dog", dog);
        modelAndView.addObject("medication", medication);
        modelAndView.addObject("editMedicationRequest", editMedicationRequest);

        return modelAndView;
    }

    @PutMapping("/{dogId}/{medicationId}/details")
    public ModelAndView updateMedication(
            @Valid @ModelAttribute("editMedicationRequest") MedicationRequest editMedicationRequest,
            BindingResult bindingResult,
            @PathVariable UUID dogId,
            @PathVariable UUID medicationId,
            @AuthenticationPrincipal UserData userData) {

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("medication-profile");

            modelAndView.addObject("medication", medicationService.getMedicationByIdAndDogId(medicationId, dogId, userData.getUserId()));
            modelAndView.addObject("dog", dogService.getDogById(editMedicationRequest.getDogId()));
            modelAndView.addObject("editMedicationRequest", editMedicationRequest);

            return modelAndView;
        }

        medicationService.updateMedication(medicationId, editMedicationRequest.getDogId(), editMedicationRequest.getName(),
                editMedicationRequest.getStartDate(), editMedicationRequest.getEndDate(), editMedicationRequest.getMedicationConcentrationMg(), userData.getUserId());

        return new ModelAndView("redirect:/medications/" + editMedicationRequest.getDogId());
    }

    @DeleteMapping("/{dogId}/{medicationId}")
    public String deleteMedication(
            @PathVariable UUID dogId,
            @PathVariable UUID medicationId,
            @AuthenticationPrincipal UserData userData) {

        medicationService.deleteMedication(medicationId, dogId, userData.getUserId());

        return "redirect:/medications/" + dogId;
    }
}
