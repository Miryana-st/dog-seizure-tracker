package app.web.user;

import app.model.dto.UserEditRequest;
import app.model.entity.user.User;
import app.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView profile(@PathVariable String id){
        User user = userService.getById(UUID.fromString(id));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView profile(@PathVariable String id, @Valid @ModelAttribute UserEditRequest userEditRequest){
        User updatedUser = userService.update(id, userEditRequest);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", updatedUser);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping()
    public ModelAndView getAllUsers(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", userService.getAllUsers());
        return modelAndView;
    }

    @PutMapping("/{id}/role")
    public ModelAndView switchUserRole(@PathVariable String id) {
        userService.switchRole(UUID.fromString(id));

        return new ModelAndView("redirect:/users");
    }
}
