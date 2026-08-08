package app.service.user;

import app.model.dto.user.UserRegisterRequest;
import app.model.entity.user.User;
import app.property.user.UserProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserInitUTest {

    @Mock
    private UserService userService;

    @Mock
    private UserProperties userProperties;

    @InjectMocks
    private UserInit userInit;

    @Test
    void whenNoUsersExist_thenRegisterDefaultUser() throws Exception {

        UserProperties.DefaultUser defaultUser = new UserProperties.DefaultUser();

        defaultUser.setUsername("admin");
        defaultUser.setPassword("password");
        defaultUser.setFirstName("Admin");
        defaultUser.setLastName("Admin");
        defaultUser.setEmail("admin@email.com");

        when(userService.getAllUsers()).thenReturn(List.of());

        when(userProperties.getDefaultUser()).thenReturn(defaultUser);

        userInit.run(null);

        ArgumentCaptor<UserRegisterRequest> userCaptor = ArgumentCaptor.forClass(UserRegisterRequest.class);

        verify(userService).registerUser(userCaptor.capture());

        UserRegisterRequest request = userCaptor.getValue();

        assertEquals("admin", request.getUsername());
        assertEquals("password", request.getPassword());
        assertEquals("Admin", request.getFirstName());
        assertEquals("Admin", request.getLastName());
        assertEquals("admin@email.com", request.getEmail());
    }

    @Test
    void whenUsersExistButDefaultUserDoesNotExist_thenRegisterDefaultUser() throws Exception {

        User existingUser = User.builder()
                .username("normalUser")
                .build();

        UserProperties.DefaultUser defaultUser = new UserProperties.DefaultUser();

        defaultUser.setUsername("admin");
        defaultUser.setPassword("password");
        defaultUser.setFirstName("Admin");
        defaultUser.setLastName("Admin");
        defaultUser.setEmail("admin@email.com");

        when(userService.getAllUsers()).thenReturn(List.of(existingUser));

        when(userProperties.getDefaultUser()).thenReturn(defaultUser);

        userInit.run(null);

        ArgumentCaptor<UserRegisterRequest> userCaptor = ArgumentCaptor.forClass(UserRegisterRequest.class);

        verify(userService).registerUser(userCaptor.capture());

        UserRegisterRequest request = userCaptor.getValue();

        assertEquals("admin", request.getUsername());
        assertEquals("password", request.getPassword());
    }
}
