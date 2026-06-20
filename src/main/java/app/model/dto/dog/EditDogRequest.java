package app.model.dto.dog;

import app.model.entity.dog.GenderDog;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditDogRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String breed;

    @URL(message = "*Invalid URL")
    private String dogPicture;

    private GenderDog gender;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    private String food;
}

