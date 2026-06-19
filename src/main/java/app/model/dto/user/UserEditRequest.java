package app.model.dto.user;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @Column(unique = true)
    private String email;

    private String phoneNumber;
}
