package app.web.seizure;

import app.service.dog.DogService;
import app.service.seizure.SeizureService;
import app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/seizures")
public class SeizureController {

    private final SeizureService seizureService;
    private final DogService dogService;
    private final UserService userService;

    @Autowired
    public SeizureController(SeizureService seizureService, DogService dogService, UserService userService) {
        this.seizureService = seizureService;
        this.dogService = dogService;
        this.userService = userService;
    }



}
