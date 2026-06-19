package app.web.user;

import app.model.dto.user.UserEditRequest;
import app.model.entity.user.User;
import app.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
    public ModelAndView getProfilePage(@PathVariable UUID id) {

        User user = userService.getById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userEditRequest", new UserEditRequest());

        return modelAndView;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView updateProfilePage(@Valid @ModelAttribute("userEditRequest") UserEditRequest userEditRequest,
                                          BindingResult result,
                                          @PathVariable UUID id) {

        if (result.hasErrors()) {

            ModelAndView modelAndView = new ModelAndView("profile");
            modelAndView.addObject("user", userService.getById(id));
            modelAndView.addObject("userEditRequest", userEditRequest);

            return modelAndView;
        }

        userService.updateUser(id, userEditRequest);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping()
    public ModelAndView getAllUsers(HttpSession session) {

        UUID userUUID = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userUUID);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", userService.getAllUsers());
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PutMapping("/{id}/role")
    public ModelAndView switchUserRole(@PathVariable UUID id) {

        userService.switchRole(id);

        return new ModelAndView("redirect:/users");
    }
}
