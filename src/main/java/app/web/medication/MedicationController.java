package app.web.medication;

import app.model.dto.medication.MedicationRequest;
import app.model.dto.medication.MedicationResponse;
import app.model.entity.dog.Dog;
import app.security.user.UserData;
import app.service.dog.DogService;
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
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView getMedicationsPage(
            @PathVariable UUID dogId,
            @AuthenticationPrincipal UserData userData) {

        ModelAndView modelAndView = new ModelAndView("medication");
        modelAndView.addObject("dogs", dogService.getAllDogsByOwnerId(userData.getUserId()));
        modelAndView.addObject("selectedDogId", dogId);
        modelAndView.addObject("dog", dogService.getDogById(dogId));
        modelAndView.addObject("dogAge", dogService.calculateDogAge(dogId));
        modelAndView.addObject("dogMedications", medicationService.getMedicationsByDogId(dogId));

        return modelAndView;
    }

    @GetMapping("/{dogId}/new")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView getAddMedicationPage(
            @PathVariable UUID dogId) {

        Dog dog = dogService.getDogById(dogId);

        ModelAndView modelAndView = new ModelAndView("add-medication");

        modelAndView.addObject("dog", dog);
        modelAndView.addObject("addMedicationRequest", new MedicationRequest());

        return modelAndView;
    }

    @PostMapping("/{dogId}/new")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView addMedicationPage(
            @Valid @ModelAttribute("addMedicationRequest") MedicationRequest addMedicationRequest,
            BindingResult bindingResult,
            @PathVariable UUID dogId) {

        Dog dog = dogService.getDogById(dogId);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("add-medication");

            modelAndView.addObject("dog", dog);
            modelAndView.addObject("addMedicationRequest", addMedicationRequest);

            return modelAndView;
        }

        medicationService.addMedication(
                dogId,
                addMedicationRequest.getName(),
                addMedicationRequest.getStartDate(),
                addMedicationRequest.getEndDate(),
                addMedicationRequest.getMedicationConcentrationMg());

        return new ModelAndView("redirect:/medications/" + dogId);
    }

    @GetMapping("/{dogId}/{medicationId}/details")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView getEditMedicationPage(
            @PathVariable UUID dogId,
            @PathVariable UUID medicationId) {

        MedicationResponse medication = medicationService.getMedicationByIdAndDogId(medicationId, dogId);

        Dog dog = dogService.getDogById(dogId);

        MedicationRequest editMedicationRequest = MedicationRequest.builder()
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
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView updateMedication(
            @Valid @ModelAttribute("editMedicationRequest") MedicationRequest editMedicationRequest,
            BindingResult bindingResult,
            @PathVariable UUID dogId,
            @PathVariable UUID medicationId) {

        if (bindingResult.hasErrors()) {

            ModelAndView modelAndView = new ModelAndView("medication-profile");

            modelAndView.addObject("medication", medicationService.getMedicationByIdAndDogId(medicationId, dogId));
            modelAndView.addObject("dog", dogService.getDogById(dogId));
            modelAndView.addObject("editMedicationRequest", editMedicationRequest);

            return modelAndView;
        }

        medicationService.updateMedication(
                medicationId,
                dogId,
                editMedicationRequest.getName(),
                editMedicationRequest.getStartDate(),
                editMedicationRequest.getEndDate(),
                editMedicationRequest.getMedicationConcentrationMg());

        return new ModelAndView("redirect:/medications/" + dogId);
    }

    @DeleteMapping("/{dogId}/{medicationId}")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public String deleteMedication(
            @PathVariable UUID dogId,
            @PathVariable UUID medicationId) {

        medicationService.deleteMedication(medicationId, dogId);

        return "redirect:/medications/" + dogId;
    }
}
