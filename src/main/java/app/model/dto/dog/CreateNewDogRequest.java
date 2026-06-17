package app.model.dto.dog;

import app.model.entity.dog.GenderDog;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CreateNewDogRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Breed cannot be empty")
    private String breed;

    @URL(message = "Invalid URL")
    private String dogPicture;

    private GenderDog gender;

    @DateTimeFormat(pattern = "MMMM d, yyyy")
    private LocalDate dateOfBirth;

    private String food;
}
