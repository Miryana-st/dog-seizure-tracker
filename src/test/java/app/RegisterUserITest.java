package app;

import app.model.dto.user.UserRegisterRequest;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.repository.user.UserRepository;
import app.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RegisterUserITest {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Test
    void registerUser_happyPath() {

        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .username("testUser1")
                .password("123456789")
                .email("testUser1@example.com")
                .build();

        User registeredUser = userService.registerUser(userRegisterRequest);

        assertNotNull(registeredUser);
        assertNotNull(registeredUser.getId());
        assertEquals(userRegisterRequest.getFirstName(), registeredUser.getFirstName());
        assertEquals(userRegisterRequest.getLastName(), registeredUser.getLastName());
        assertEquals(userRegisterRequest.getUsername(), registeredUser.getUsername());
        assertEquals(userRegisterRequest.getEmail(), registeredUser.getEmail());
        assertTrue(passwordEncoder.matches(userRegisterRequest.getPassword(), registeredUser.getPassword()));
        assertEquals(UserRole.USER, registeredUser.getRole());

        User userFromDatabase = userRepository.findById(registeredUser.getId()) .orElseThrow();

        assertEquals(registeredUser.getId(), userFromDatabase.getId());
        assertEquals(registeredUser.getFirstName(), userFromDatabase.getFirstName());
        assertEquals(registeredUser.getLastName(), userFromDatabase.getLastName());
        assertEquals(registeredUser.getUsername(), userFromDatabase.getUsername());
        assertEquals(registeredUser.getEmail(), userFromDatabase.getEmail());
        assertEquals(registeredUser.getRole(), userFromDatabase.getRole());
        assertTrue(passwordEncoder.matches(userRegisterRequest.getPassword(), userFromDatabase.getPassword()));
    }
}
