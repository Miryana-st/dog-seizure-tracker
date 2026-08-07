package app.model.dto.user;

import app.model.entity.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserDtoMapperUTest {

    @Test
    void fromUserToEditUserRequest_whenUserWithDetailsIsPassed_thenDtoIsReturnedWithSameDetails() {

        User user = User.builder()
                .firstName("test name")
                .lastName("last name")
                .email("email..email@email.com")
                .phoneNumber("123-456-786")
                .build();

        UserEditRequest result = UserDtoMapper.fromUser(user);

        assertEquals("test name", result.getFirstName());
        assertEquals("last name", result.getLastName());
        assertEquals("email..email@email.com", result.getEmail());
        assertEquals("123-456-786", result.getPhoneNumber());
    }
}
