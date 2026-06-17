package app.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEditRequest {

    @Size(min = 3, message = "*First Name must be at least 3 characters long")
    private String firstName;

    @Size(min = 3, message = "*Last Name must be at least 3 characters long")
    private String lastName;

    @Email(message = "*Email should be valid")
    private String email;

    private String phoneNumber;
}
