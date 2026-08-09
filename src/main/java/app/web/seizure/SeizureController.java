package app.web.seizure;

import app.model.dto.seizure.CreateNewSeizureRequest;
import app.model.dto.seizure.EditSeizureRequest;
import app.model.dto.seizure.SeizureDtoMapper;
import app.model.entity.dog.Dog;
import app.model.entity.seizure.Seizure;
import app.service.dog.DogService;
import app.service.pdf.PdfService;
import app.service.seizure.SeizureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/dogs/{dogId}/seizures")
public class SeizureController {

    private final SeizureService seizureService;
    private final DogService dogService;
    private final PdfService pdfService;

    @Autowired
    public SeizureController(SeizureService seizureService, DogService dogService, PdfService pdfService) {
        this.seizureService = seizureService;
        this.dogService = dogService;
        this.pdfService = pdfService;
    }

    @GetMapping()
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView getSeizuresForDog(@PathVariable UUID dogId) {

        Dog dog = dogService.getDogById(dogId);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("seizures");
        modelAndView.addObject("dog", dog);
        modelAndView.addObject("seizures", seizureService.getAllSeizuresByDog_IdOrderByDateDescTimeDesc(dogId));

        return modelAndView;
    }

    @GetMapping("/new")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView getNewSeizurePage(@PathVariable UUID dogId) {

        Dog dog = dogService.getDogById(dogId);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("add-seizure");
        modelAndView.addObject("dog", dog);
        modelAndView.addObject("createNewSeizureRequest", new CreateNewSeizureRequest());

        return modelAndView;
    }

    @PostMapping()
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView createNewSeizurePage(@Valid @ModelAttribute("createNewSeizureRequest") CreateNewSeizureRequest createNewSeizureRequest,
                                             BindingResult result,
                                             @PathVariable UUID dogId) {

        Dog dog = dogService.getDogById(dogId);

        if (result.hasErrors()) {

            ModelAndView modelAndView = new ModelAndView("add-seizure");

            modelAndView.addObject("dog", dog);
            modelAndView.addObject("createNewSeizureRequest", createNewSeizureRequest);

            return modelAndView;
        }

        seizureService.createSeizureEntry(createNewSeizureRequest, dog);

        return new ModelAndView("redirect:/dogs/" + dogId + "/seizures");
    }

    @GetMapping("/{seizureId}/details")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView getSeizureLog (@PathVariable UUID dogId,
                                      @PathVariable UUID seizureId) {

        Dog dog = dogService.getDogById(dogId);
        Seizure seizure = seizureService.getSeizureById(seizureId);
        EditSeizureRequest editSeizureRequest = SeizureDtoMapper.fromSeizure(seizure);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("seizure-profile");
        modelAndView.addObject("seizure", seizure);
        modelAndView.addObject("dog", dog);
        modelAndView.addObject("editSeizureRequest", editSeizureRequest);

        return modelAndView;
    }

    @PutMapping("/{seizureId}/seizure-profile")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ModelAndView updateSeizureLog(@PathVariable UUID dogId,
                                         @PathVariable UUID seizureId,
                                         @Valid @ModelAttribute("editSeizureRequest") EditSeizureRequest editSeizureRequest,
                                         BindingResult result) {

        if (result.hasErrors()) {

            ModelAndView modelAndView = new ModelAndView("seizure-profile");

            Dog dog = dogService.getDogById(dogId);

            Seizure seizure = seizureService.getSeizureById(seizureId);

            modelAndView.addObject("seizure", seizure);
            modelAndView.addObject("dog", dog);
            modelAndView.addObject("editSeizureRequest", editSeizureRequest);

            return modelAndView;
        }

        seizureService.updateSeizureEntry(seizureId, editSeizureRequest);

        return new ModelAndView("redirect:/dogs/" + dogId + "/seizures");
    }

    @DeleteMapping("/{seizureId}")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public String deleteSeizure(@PathVariable UUID dogId, @PathVariable UUID seizureId) {

        seizureService.deleteSeizureById(seizureId);

        return "redirect:/dogs/" + dogId + "/seizures";
    }

    @GetMapping("/pdf")
    @PreAuthorize("@dogService.isDogOwner(#dogId, authentication.principal.userId)")
    public ResponseEntity<byte[]> exportSeizureReport(@PathVariable UUID dogId) {

        byte[] pdf = pdfService.generateSeizureReport(dogId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=seizure-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
