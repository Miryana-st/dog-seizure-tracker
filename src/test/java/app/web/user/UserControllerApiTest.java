package app.web.user;

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

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUsersEndpointWithAdmin_shouldReturn200OkAndUsersView() throws Exception {

        User adminUser = aRandomUser();
        adminUser.setRole(UserRole.ADMIN);

        User regularUser = aRandomUser();
        regularUser.setRole(UserRole.USER);

        UserData authorizedUser = new UserData(
                adminUser.getId(),
                adminUser.getUsername(),
                adminUser.getPassword(),
                adminUser.getRole()
        );

        when(userService.getById(adminUser.getId())).thenReturn(adminUser);
        when(userService.getAllUsers())
                .thenReturn(List.of(adminUser, regularUser));

        MockHttpServletRequestBuilder httpRequest = get("/users")
                .with(user(authorizedUser));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attribute("user", adminUser))
                .andExpect(model().attribute("users", List.of(adminUser, regularUser)));
    }

    @Test
    void switchUserRole_shouldSwitchRoleAndRedirectToUsers() throws Exception {

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
