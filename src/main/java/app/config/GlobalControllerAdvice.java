package app.config;

import app.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFound.class)
    public ModelAndView handleUserNotFoundException(UserNotFound e) {

        ModelAndView modelAndView = new ModelAndView("error-page-not-found");

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DogNotFound.class)
    public ModelAndView handleDogNotFoundException(DogNotFound e) {

        ModelAndView modelAndView = new ModelAndView("error-page-not-found");

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SeizureNotFound.class)
    public ModelAndView handleSeizureNotFoundException(SeizureNotFound e) {

        ModelAndView modelAndView = new ModelAndView("error-page-not-found");

        return modelAndView;
    }

    @ExceptionHandler(UserWithEmailOrUsernameExists.class)
    public String handleUserWithEmailOrUsernameExistsException(UserWithEmailOrUsernameExists e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler({
            NoResourceFoundException.class,
            AccessDeniedException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ModelAndView handleSpringException(Exception exception) {

        ModelAndView modelAndView = new ModelAndView("error-page-not-found");

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MedicationNotFoundException.class)
    public ModelAndView handleMedicationNotFoundException(MedicationNotFoundException e) {

        ModelAndView modelAndView = new ModelAndView("error-page-not-found");

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MedicationScheduleNotFoundException.class)
    public ModelAndView handleMedicationScheduleNotFoundException(MedicationScheduleNotFoundException e) {

        ModelAndView modelAndView = new ModelAndView("error-page-not-found");

        return modelAndView;
    }

    @ExceptionHandler(MedicationMicroserviceUnavailableException.class)
    public ModelAndView handleMedicationMicroserviceUnavailableException(MedicationMicroserviceUnavailableException e) {

        ModelAndView modelAndView = new ModelAndView("error-page");

        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleLeftoverExceptions(Exception e) {

        ModelAndView modelAndView = new ModelAndView("error-page");

        return modelAndView;
    }
}
