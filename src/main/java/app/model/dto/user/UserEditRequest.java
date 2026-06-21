package app.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditRequest {

    @NotBlank
    @Size(min = 3, max = 20, message = "*First name must be between 3 and 20 characters long")
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 20, message = "*Last name must be between 3 and 20 characters long")
    private String lastName;

    @NotBlank
    @Email(message = "*Email should be valid")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "*Email must be in the format name@example.com"
    )
    private String email;

    @Pattern(
            regexp = "^$|\\+?[0-9\\s-]{8,20}$",
            message = "*Please enter a valid phone number"
    )
    private String phoneNumber;
}
