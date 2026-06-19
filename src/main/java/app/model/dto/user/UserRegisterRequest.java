package app.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {

    @NotBlank(message = "*First name is required")
    @Size(min = 3, max = 20, message = "*First name must be between 3 and 20 characters long")
    private String firstName;

    @NotBlank(message = "*Last name is required")
    @Size(min = 3, max = 20, message = "*Last name must be between 3 and 20 characters long")
    private String lastName;

    @NotBlank(message = "*Username is required")
    @Size(min = 3, message = "*Username must be at least 3 characters long")
    private String username;

    @NotBlank(message = "*Password is required")
    @Size(min = 3, message = "*Password must be at least 3 characters long")
    private String password;

    @NotBlank(message = "*Email is required")
    @Email(message = "*Email should be valid")
    private String email;
}
