package app.web.dog;

import app.model.dto.dog.CreateNewDogRequest;
import app.model.dto.dog.EditDogRequest;
import app.model.entity.dog.Dog;
import app.model.entity.user.User;
import app.service.dog.DogService;
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
@RequestMapping("/dogs")
public class DogController {

    private final DogService dogService;
    private final UserService userService;

    @Autowired
    public DogController(DogService dogService, UserService userService) {
        this.dogService = dogService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getUserDogsPage(HttpSession session) {

        UUID userUUID = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userUUID);

        ModelAndView modelAndView = new ModelAndView("dogs");
        modelAndView.addObject("user", user);
        modelAndView.addObject("dogs", dogService.getAllDogsByOwnerId(userUUID));

        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView getNewDogPage(HttpSession session) {

        UUID userUUID = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userUUID);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-dog");
        modelAndView.addObject("user", user);
        modelAndView.addObject("createNewDogRequest", new CreateNewDogRequest());

        return modelAndView;
    }

    @PostMapping()
    public ModelAndView createNewDogPage(@Valid @ModelAttribute("createNewDogRequest") CreateNewDogRequest createNewDogRequest,
                                         BindingResult result,
                                         HttpSession session) {

        if (result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("add-dog");

            modelAndView.addObject("createNewDogRequest", createNewDogRequest);

            UUID userUUID = (UUID) session.getAttribute("user_id");
            modelAndView.addObject("user", userService.getById(userUUID));

            return modelAndView;
        }

        UUID userUUID = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userUUID);

        dogService.createDog(createNewDogRequest, user);

        return new ModelAndView("redirect:/dogs");
    }

    @GetMapping("/{id}/dog-profile")
    public ModelAndView getDogProfilePage(@PathVariable UUID id,
                                       HttpSession session){

        UUID userUUID = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userUUID);

        Dog dog = dogService.getDogById(id);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("dog-profile");

        modelAndView.addObject("user", user);
        modelAndView.addObject("dog", dog);
        modelAndView.addObject("editDogRequest", new EditDogRequest());

        return modelAndView;
    }

    @PutMapping("/{id}/dog-profile")
    public ModelAndView updateDogProfilePage(@Valid @ModelAttribute("editDogRequest") EditDogRequest editDogRequest,
                                             BindingResult bindingResult,
                                             @PathVariable UUID id){
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("dog-profile");

            Dog dog = dogService.getDogById(id);

            modelAndView.addObject("dog", dog);
            modelAndView.addObject("editDogRequest", editDogRequest);

            return modelAndView;
        }

        dogService.updateDogInformation(id, editDogRequest);

        return new ModelAndView("redirect:/dogs");
    }

}
