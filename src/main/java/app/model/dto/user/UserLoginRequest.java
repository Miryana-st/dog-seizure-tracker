package app.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {

    @NotBlank
    @Size(min = 3, message = "*Username must be at least 3 characters long")
    private String username;

    @NotBlank
    @Size(min = 3, message = "*Password must be at least 3 characters long")
    private String password;
}
