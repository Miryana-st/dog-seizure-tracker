package app.web.user;

import app.model.dto.user.UserDtoMapper;
import app.model.dto.user.UserEditRequest;
import app.model.entity.user.User;
import app.security.user.UserData;
import app.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal UserData userData) {

        User user = userService.getById(userData.getUserId());

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

    @GetMapping("/{id}/details")
    @PreAuthorize("@userService.isUserOwned(#id, authentication.principal.userId) or hasRole('ADMIN')")
    public ModelAndView getProfilePage(@PathVariable UUID id) {

        User user = userService.getById(id);
        UserEditRequest userEditRequest = UserDtoMapper.fromUser(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userEditRequest", userEditRequest);

        return modelAndView;
    }

    @PutMapping("/{id}/details")
    @PreAuthorize("@userService.isUserOwned(#id, authentication.principal.userId) or hasRole('ADMIN')")
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

    @DeleteMapping("/{id}")
    @PreAuthorize("@userService.isUserOwned(#id, authentication.principal.userId) or hasRole('ADMIN')")
    public String deleteUser(@PathVariable UUID id, @AuthenticationPrincipal UserData userData) {

        userService.deleteUserById(id);

        if (id.equals(userData.getUserId())) {
            return "redirect:/";
        }

        return "redirect:/users";
    }
}
