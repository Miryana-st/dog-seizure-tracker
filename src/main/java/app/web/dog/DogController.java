package app.web.dog;

import app.model.dto.dog.CreateNewDogRequest;
import app.model.dto.dog.EditDogRequest;
import app.model.entity.dog.Dog;
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
@RequestMapping("/dogs")
public class DogController {

    private final DogService dogService;
    private final UserService userService;
    private final SeizureService seizureService;

    @Autowired
    public DogController(DogService dogService, UserService userService, SeizureService seizureService) {
        this.dogService = dogService;
        this.userService = userService;
        this.seizureService = seizureService;
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
    public String createNewDog(@Valid CreateNewDogRequest createNewDogRequest,
                               BindingResult result,
                               HttpSession session) {

        if (result.hasErrors()) {
            return "add-dog";
        }

        UUID userUUID = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userUUID);

        dogService.create(createNewDogRequest, user);

        return "redirect:/dogs";
    }

    @GetMapping("/{id}/dog-profile")
    public ModelAndView dogProfile (@PathVariable String id){

        Dog dog = dogService.getDogById(UUID.fromString(id));

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("dog-profile");
        modelAndView.addObject("dog", dog);

        return modelAndView;
    }

    @PutMapping("/{id}/dog-profile")
    public ModelAndView dogProfile(@PathVariable String id, @Valid @ModelAttribute EditDogRequest editDogRequest){
        Dog updatedDog = dogService.update(id, editDogRequest);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("dog", updatedDog);

        return new ModelAndView("redirect:/dogs");
    }

}
