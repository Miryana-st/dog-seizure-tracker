package app.service.user;

import app.exception.NotFoundException;
import app.exception.UserWithEmailOrUsernameExists;
import app.model.dto.user.UserEditRequest;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.repository.user.UserRepository;
import app.security.user.UserData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void whenEditUserDetails_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID userId = UUID.randomUUID();
        UserEditRequest dto = null;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, dto));
    }

    @Test
    void whenEditUserDetails_andRepositoryReturnsUserFromTheDatabase_thenUpdateTheUserDetailsAndSaveUpdatedUser() {

        UUID userId = UUID.randomUUID();
        UserEditRequest dto = UserEditRequest.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@email.com")
                .phoneNumber("123-456-7890")
                .build();

        User userRetrievedFromDatabase = User.builder()
                .id(userId)
                .firstName("First")
                .lastName("Last")
                .email("emailemail@emailemail.com")
                .phoneNumber(null)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userRetrievedFromDatabase));

        userService.updateUser(userId, dto);

        assertEquals("FirstName", userRetrievedFromDatabase.getFirstName());
        assertEquals("LastName", userRetrievedFromDatabase.getLastName());
        assertEquals("email@email.com", userRetrievedFromDatabase.getEmail());
        assertNotNull(userRetrievedFromDatabase.getPhoneNumber());
        assertEquals("123-456-7890", userRetrievedFromDatabase.getPhoneNumber());
        verify(userRepository).save(userRetrievedFromDatabase);
    }

    @Test
    void whenDeletingUserById_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUserById(userId));
    }

    @Test
    void whenDeletingUserById_andRepositoryReturnsUserFromTheDatabase_thenDeleteTheUser() {

        UUID userId = UUID.randomUUID();

        User userRetrievedFromDatabase = User.builder()
                .id(userId)
                .firstName("First")
                .lastName("Last")
                .email("emailemail@emailemail.com")
                .phoneNumber("123-456-7890")
                .role(UserRole.USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userRetrievedFromDatabase));

        userService.deleteUserById(userId);

        verify(userRepository).delete(userRetrievedFromDatabase);
    }

    @Test
    void whenGetAllUsers_andRepositoryReturnsUsers_thenReturnAllUsers() {

        User firstUser = new User();
        User secondUser = new User();

        List<User> users = List.of(firstUser, secondUser);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users, result);
        verify(userRepository).findAll();
    }

    @Test
    void whenGetAllUsers_andRepositoryReturnsEmptyList_thenReturnEmptyList() {

        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void whenUserIdEqualsLoggedUserId_thenReturnTrue() {

        UUID userId = UUID.randomUUID();

        boolean result = userService.isUserOwned(userId, userId);

        assertTrue(result);
    }

    @Test
    void whenUserIdDiffersFromLoggedUserId_thenReturnFalse() {

        UUID userId = UUID.randomUUID();
        UUID loggedUserId = UUID.randomUUID();

        boolean result = userService.isUserOwned(userId, loggedUserId);

        assertFalse(result);
    }

    @Test
    void whenLoadUserByUsername_andUserExists_thenReturnUserData() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername(user.getUsername());

        assertNotNull(result);
        assertInstanceOf(UserData.class, result);

        UserData userData = (UserData) result;

        assertEquals(user.getId(), userData.getUserId());
        assertEquals(user.getUsername(), userData.getUsername());
        assertEquals(user.getPassword(), userData.getPassword());
        assertEquals(user.getRole(), userData.getRole());

        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    void whenLoadUserByUsername_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        String username = "unknownUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    void whenSwitchRole_andUserHasUserRole_thenChangeRoleToAdminAndSave() {

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .role(UserRole.USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchRole(userId);

        assertEquals(UserRole.ADMIN, user.getRole());

        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchRole_andUserHasAdminRole_thenChangeRoleToUserAndSave() {

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchRole(userId);

        assertEquals(UserRole.USER, user.getRole());

        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchRole_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.switchRole(userId));
    }

    @Test
    void whenRegisterUser_andRepositoryReturnsExistingUser_thenThrowsException() {

        UserRegisterRequest dto = UserRegisterRequest.builder()
                .username("testUser")
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@email.com")
                .password("password")
                .build();

        when(userRepository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail()))
                .thenReturn(Optional.of(new User()));

        assertThrows(UserWithEmailOrUsernameExists.class, () -> userService.registerUser(dto));
    }

    @Test
    void whenRegisterUser_andNoUserWithSameEmailOrUsernameInRepository_thenRegisterUser() {

        UserRegisterRequest dto = UserRegisterRequest.builder()
                .username("testUser")
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@email.com")
                .password("password")
                .build();

        when(userRepository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail()))
                .thenReturn(Optional.empty());
        when(userRepository.findAll()).thenReturn(List.of(new User()));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .username("testUser")
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@email.com")
                .password("encodedPassword")
                .role(UserRole.USER).build();

        userService.registerUser(dto);

        assertEquals("testUser", savedUser.getUsername());
        assertEquals("FirstName", savedUser.getFirstName());
        assertEquals("LastName", savedUser.getLastName());
        assertEquals("email@email.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(UserRole.USER, savedUser.getRole());

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void whenRegisterUser_andNoUserInRepository_thenRegisterUserWithRoleAdmin() {

        UserRegisterRequest dto = UserRegisterRequest.builder()
                .username("testUser")
                .email("email@email.com")
                .build();

        when(userRepository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail()))
                .thenReturn(Optional.empty());
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        User savedUser = User.builder().role(UserRole.ADMIN).build();

        userService.registerUser(dto);

        assertEquals(UserRole.ADMIN, savedUser.getRole());

        verify(userRepository).save(any(User.class));
    }
}
