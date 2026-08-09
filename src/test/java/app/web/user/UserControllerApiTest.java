package app.web.user;

import app.model.dto.user.UserEditRequest;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.security.user.UserData;
import app.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean(name = "userService")
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllUsersEndpointWithAdmin_shouldReturn200OkAndUsersView() throws Exception {

        User adminUser = aRandomUser();
        adminUser.setRole(UserRole.ADMIN);

        User regularUser = aRandomUser();
        regularUser.setRole(UserRole.USER);

        UserData authorizedUser = new UserData(
                adminUser.getId(),
                adminUser.getUsername(),
                adminUser.getPassword(),
                adminUser.getRole());

        when(userService.getById(adminUser.getId())).thenReturn(adminUser);
        when(userService.getAllUsers()).thenReturn(List.of(adminUser, regularUser));

        MockHttpServletRequestBuilder httpRequest = get("/users")
                .with(user(authorizedUser));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attribute("user", adminUser))
                .andExpect(model().attribute("users", List.of(adminUser, regularUser)));
    }

    @Test
    void putSwitchUserRoleEndpoint_shouldSwitchRoleAndRedirectToUsers() throws Exception {

        UUID userId = UUID.randomUUID();
        UserData authorizedUser = admin();

        MockHttpServletRequestBuilder httpRequest = put("/users/{id}/role", userId)
                .with(user(authorizedUser))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users"));

        verify(userService).switchRole(userId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getProfileEndpoint_whenUserOwnsProfile_shouldReturnProfileView() throws Exception {

        User user = aRandomUser();

        UserData authentication = new UserData(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole());

        when(userService.isUserOwned(user.getId(), authentication.getUserId())).thenReturn(true);
        when(userService.getById(user.getId())).thenReturn(user);

        MockHttpServletRequestBuilder httpRequest =
                get("/users/{id}/details", user.getId())
                        .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("userEditRequest", "user"));

        verify(userService).isUserOwned(user.getId(), authentication.getUserId());
        verify(userService).getById(user.getId());
    }

    @Test
    void getProfileEndpoint_whenAccessDeniedExceptionOccurs_shouldReturnNotFoundErrorPage() throws Exception {

        User user = aRandomUser();

        UserData authentication = new UserData(
                UUID.randomUUID(),
                "normalUser",
                "password",
                UserRole.USER);

        when(userService.isUserOwned(user.getId(), authentication.getUserId())).thenThrow(AccessDeniedException.class);

        MockHttpServletRequestBuilder httpRequest =
                get("/users/{id}/details", user.getId())
                        .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"));

        verify(userService).isUserOwned(user.getId(), authentication.getUserId());
        verify(userService, never()).getById(any());
    }

    @Test
    void putUpdateProfileEndpoint_whenValidRequest_shouldUpdateUserAndRedirectToHome() throws Exception {

        User user = aRandomUser();

        UserData authorizedUser = new UserData(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole());

        when(userService.isUserOwned(user.getId(), authorizedUser.getUserId())).thenReturn(true);

        MockHttpServletRequestBuilder httpRequest = put("/users/{id}/details", user.getId())
                .with(user(authorizedUser))
                .with(csrf())
                .param("firstName", "UpdatedFirstName")
                .param("lastName", "UpdatedLastName")
                .param("email", "updated@mail.com")
                .param("phoneNumber", "123456789");

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/home"));

        verify(userService).updateUser(eq(user.getId()), any(UserEditRequest.class));
    }

    @Test
    void putUpdateProfileEndpoint_whenValidationErrors_shouldReturnProfileView() throws Exception {

        User user = aRandomUser();

        UserData authorizedUser = new UserData(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole());

        when(userService.isUserOwned(user.getId(), authorizedUser.getUserId())).thenReturn(true);
        when(userService.getById(user.getId())).thenReturn(user);

        MockHttpServletRequestBuilder httpRequest = put("/users/{id}/details", user.getId())
                .with(user(authorizedUser))
                .with(csrf())
                .param("firstName", "")
                .param("lastName", "UpdatedLastName")
                .param("email", "updated@mail.com")
                .param("phoneNumber", "123456789");

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attributeExists("userEditRequest"));
    }

    @Test
    void deleteUserEndpoint_whenDeletingOwnAccount_shouldReturnRedirectToHomePage() throws Exception {

        User user = aRandomUser();

        UserData authorizedUser = new UserData(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole());

        when(userService.isUserOwned(user.getId(), authorizedUser.getUserId())).thenReturn(true);

        MockHttpServletRequestBuilder httpRequest = delete("/users/{id}", user.getId())
                .with(user(authorizedUser))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        verify(userService).deleteUserById(user.getId());
    }

    @Test
    void deleteUserEndpoint_whenAdminDeletesAnotherUser_shouldReturnRedirectToUsers() throws Exception {

        User user = aRandomUser();

        UserData authorizedUser = admin();

        MockHttpServletRequestBuilder httpRequest =
                delete("/users/{id}", user.getId())
                .with(user(authorizedUser))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users"));

        verify(userService).deleteUserById(user.getId());
    }

    public static User aRandomUser() {

        return User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .firstName("FirstName")
                .lastName("LastName")
                .password("RandomPassword")
                .email("testMail@mail.com")
                .role(UserRole.USER)
                .build();
    }

    public static UserData admin() {

        return new UserData(
                UUID.randomUUID(),
                "admin",
                "RandomPassword",
                UserRole.ADMIN);
    }
}
