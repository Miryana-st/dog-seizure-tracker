package app.web.seizure;

import app.model.dto.dog.CreateNewDogRequest;
import app.model.dto.seizure.CreateNewSeizureRequest;
import app.model.entity.user.User;
import app.service.seizure.SeizureService;
import app.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/seizures")
public class SeizureController {

    private final SeizureService seizureService;
    private final UserService userService;

    @Autowired
    public SeizureController(SeizureService seizureService, UserService userService) {
        this.seizureService = seizureService;
        this.userService = userService;
    }

    @GetMapping("/dog/{id}")
    public ModelAndView getSeizuresPerDog(@PathVariable String id, HttpSession session) {

        UUID userUUID = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userUUID);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("seizures");
        modelAndView.addObject("seizures", seizureService.getAllSeizuresByDogId(UUID.fromString(id)));

        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView getNewSeizurePage(HttpSession session) {

        UUID userUUID = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userUUID);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-seizure");
        modelAndView.addObject("user", user);
        modelAndView.addObject("createNewSeizureRequest", new CreateNewSeizureRequest());

        return modelAndView;
    }

}
