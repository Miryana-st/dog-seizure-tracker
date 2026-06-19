package app.web.seizure;

import app.model.dto.seizure.CreateNewSeizureRequest;
import app.model.dto.seizure.EditSeizureRequest;
import app.model.entity.dog.Dog;
import app.model.entity.seizure.Seizure;
import app.model.entity.user.User;
import app.service.dog.DogService;
import app.service.seizure.SeizureService;
import app.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/dogs/{dogId}/seizures")
public class SeizureController {

    private final SeizureService seizureService;
    private final UserService userService;
    private final DogService dogService;

    @Autowired
    public SeizureController(SeizureService seizureService, UserService userService, DogService dogService) {
        this.seizureService = seizureService;
        this.userService = userService;
        this.dogService = dogService;
    }

    @GetMapping()
    public ModelAndView getSeizuresForDog(@PathVariable UUID dogId,
                                          HttpSession session) {

        UUID userUUID = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userUUID);

        Dog dog = dogService.getDogById(dogId);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("user", user);
        modelAndView.addObject("dog", dog);
        modelAndView.setViewName("seizures");
        modelAndView.addObject("seizures", seizureService.findAllByDog_IdOrderByDateDescTimeDesc(dogId));

        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView getNewSeizurePage(@PathVariable UUID dogId,
                                          HttpSession session) {

        UUID userUUID = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userUUID);

        Dog dog = dogService.getDogById(dogId);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("add-seizure");
        modelAndView.addObject("user", user);
        modelAndView.addObject("dog", dog);
        modelAndView.addObject("createNewSeizureRequest", new CreateNewSeizureRequest());

        return modelAndView;
    }

    @PostMapping()
    public ModelAndView createNewSeizurePage(@Valid @ModelAttribute("createNewSeizureRequest") CreateNewSeizureRequest createNewSeizureRequest,
                                             BindingResult result,
                                             @PathVariable UUID dogId,
                                             HttpSession session) {

        Dog dog = dogService.getDogById(dogId);

        if (result.hasErrors()) {

            UUID userUUID = (UUID) session.getAttribute("user_id");

            User user = userService.getById(userUUID);

            ModelAndView modelAndView = new ModelAndView("add-seizure");

            modelAndView.addObject("user", user);
            modelAndView.addObject("dog", dog);
            modelAndView.addObject("createNewSeizureRequest", createNewSeizureRequest);

            return modelAndView;
        }

        seizureService.createSeizureEntry(createNewSeizureRequest, dog);

        return new ModelAndView("redirect:/dogs/" + dogId + "/seizures");
    }

    @GetMapping("/{seizureId}/seizure-profile")
    public ModelAndView getSeizureLog(@PathVariable UUID dogId,
                                      @PathVariable UUID seizureId,
                                      HttpSession session) {

        UUID userUUID = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userUUID);

        Dog dog = dogService.getDogById(dogId);

        Seizure seizure = seizureService.getSeizureById(seizureId);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("seizure-profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("dog", dog);
        modelAndView.addObject("seizure", seizure);
        modelAndView.addObject("editSeizureRequest", new EditSeizureRequest());

        return modelAndView;
    }

    @PutMapping("/{seizureId}/seizure-profile")
    public ModelAndView updateSeizureLog(@PathVariable UUID dogId,
                                         @PathVariable UUID seizureId,
                                         @Valid @ModelAttribute("editSeizureRequest") EditSeizureRequest editSeizureRequest,
                                         BindingResult result) {

        if (result.hasErrors()) {

            ModelAndView modelAndView = new ModelAndView("seizure-profile");

            Dog dog = dogService.getDogById(dogId);
            Seizure seizure = seizureService.getSeizureById(seizureId);

            modelAndView.addObject("dog", dog);
            modelAndView.addObject("seizure", seizure);
            modelAndView.addObject("editSeizureRequest", editSeizureRequest);

            return modelAndView;
        }

        seizureService.updateSeizureEntry(seizureId, editSeizureRequest);

        return new ModelAndView("redirect:/dogs/" + dogId + "/seizures");
    }
}
