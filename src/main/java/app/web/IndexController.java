package app.web;

import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.user.User;
import app.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class IndexController {

    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("userRegisterRequest", new UserRegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerNewUser(@Valid UserRegisterRequest userRegisterRequest,
                                        BindingResult result, HttpSession session) {

        if (result.hasErrors()) {
            return new ModelAndView("register");
        }

        userService.registerUser(userRegisterRequest);

        if (session.getAttribute("user_id") != null) {
            return new ModelAndView("redirect:/users");
        }

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("userLoginRequest", new UserLoginRequest());

        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView loginUser(@Valid UserLoginRequest userLoginRequest,
                                  BindingResult result,
                                  HttpSession session) {

        if (result.hasErrors()) {
            return new ModelAndView("login");
        }

        User user = userService.loginUser(userLoginRequest);
        session.setAttribute("user_id", user.getId());

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(HttpSession session) {

        UUID userUUID = (UUID) session.getAttribute("user_id");
        User user = userService.getById(userUUID);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();
        return "redirect:/";
    }

    @DeleteMapping("/delete")
    public String deleteProfile(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");

        userService.deleteUserById(userId);

        session.invalidate();

        return "redirect:/";
    }

    @DeleteMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable UUID id, HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");

        userService.deleteUserById(id);

        if (id.equals(userId)) {

            session.invalidate();

            return "redirect:/";
        }

        return "redirect:/users";
    }
}

