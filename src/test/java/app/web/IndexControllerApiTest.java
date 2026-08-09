package app.web;

import app.exception.NotFoundException;
import app.exception.UnauthorizedException;
import app.exception.UserWithEmailOrUsernameExists;
import app.model.dto.user.UserRegisterRequest;
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

import java.util.UUID;

import static app.exception.ExceptionMessages.USER_WITH_EMAIL_OR_USERNAME_EXISTS;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getIndexEndpoint_shouldReturn200OkAndIndexView() throws Exception {

        MockHttpServletRequestBuilder httpRequest = get("/");

        mockMvc.perform(httpRequest)
                .andExpect(view().name("index"))
                .andExpect(status().isOk());
    }

    @Test
    void getRegisterEndpoint_shouldReturn200OkAndRegisterView() throws Exception {

        MockHttpServletRequestBuilder httpRequest = get("/register");

        mockMvc.perform(httpRequest)
                .andExpect(view().name("register"))
                .andExpect(status().isOk());
    }

    @Test
    void postRegisterNewUserEndpoint_shouldReturn302RedirectAndRedirectToLoginAndInvokeRegisterServiceMethod() throws Exception {

        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("firstName", "testFirstName")
                .formField("lastName", "testLastName")
                .formField("username", "testUser")
                .formField("password", "testPass")
                .formField("email", "testMail@mail.com")
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).registerUser(any());
    }

    @Test
    void postRegisterNewUserEndpointWithInvalidFormData_shouldReturn200OkAndShowRegisterViewAndRegisterServiceMethodIsNeverInvoke() throws Exception {

        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("firstName", "t")
                .formField("lastName", "t")
                .formField("username", "t")
                .formField("password", "t")
                .formField("email", "t")
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).registerUser(any());
    }

    @Test
    void postRegisterNewUserEndpointWhenUserWithEmailOrUsernameExists_shouldRedirectToRegisterAndAddFlashAttribute() throws Exception {

        UserWithEmailOrUsernameExists exception =
                new UserWithEmailOrUsernameExists(USER_WITH_EMAIL_OR_USERNAME_EXISTS);

        doThrow(exception)
                .when(userService)
                .registerUser(any(UserRegisterRequest.class));

        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("firstName", "FirstName")
                .formField("lastName", "LastName")
                .formField("username", "testUser")
                .formField("password", "testPassword")
                .formField("email", "test@email.com")
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute(
                        "errorMessage",
                        USER_WITH_EMAIL_OR_USERNAME_EXISTS
                ));

        verify(userService).registerUser(any(UserRegisterRequest.class));
    }

    @Test
    void postRegisterNewUserEndpointWhenDoneByAdminFromUsersPage_shouldReturn302RedirectAndRedirectToUsersAndInvokeRegisterServiceMethod() throws Exception {

        User user = aRandomUser();

        UserData authentication = new UserData(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                UserRole.ADMIN);

        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("firstName", "testFirstName")
                .formField("lastName", "testLastName")
                .formField("username", "testUser")
                .formField("password", "testPass")
                .formField("email", "testMail@mail.com")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService).registerUser(any(UserRegisterRequest.class));
    }

    @Test
    void getLoginEndpoint_whenNoErrorMessage_thenReturnLoginPage() throws Exception {

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("userLoginRequest"))
                .andExpect(model().attribute("loginAttemptMessage", nullValue()))
                .andExpect(model().attribute("errorMessage", nullValue()));
    }

    @Test
    void getLoginEndpoint_whenLoginError_thenReturnLoginPageWithErrorMessage() throws Exception {

        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("userLoginRequest"))
                .andExpect(model().attribute("errorMessage", "*Invalid username or password."));
    }

    @Test
    void getHomeEndpoint_shouldReturnHomeViewWithUserModelAttributeAndStatusCodeIs200() throws Exception {

        User user = aRandomUser();

        when(userService.getById(any())).thenReturn(user);

        UserData authentication = new UserData(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole());

        MockHttpServletRequestBuilder httpRequest = get("/home")
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void getHomeEndpointAndUserNotFound_ShouldReturnNotFoundErrorView() throws Exception {

        User user = aRandomUser();

        when(userService.getById(any())).thenThrow(NotFoundException.class);

        UserData authentication = new UserData(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole());

        MockHttpServletRequestBuilder httpRequest = get("/home")
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isNotFound())
                .andExpect(view().name("error-page-not-found"));
    }

    @Test
    void getHomeEndpointAndUnauthorizedUser_ShouldReturnUnauthorizedException() throws Exception {

        User user = aRandomUser();

        when(userService.getById(any())).thenThrow(UnauthorizedException.class);

        UserData authentication = new UserData(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole());

        MockHttpServletRequestBuilder httpRequest = get("/home")
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(view().name("error-page-not-found"));
    }

    @Test
    void getHomeEndpoint_whenUnexpectedExceptionOccurs_shouldReturnErrorPage() throws Exception {

        User user = aRandomUser();

        when(userService.getById(any())).thenThrow(new RuntimeException("Unexpected error"));

        UserData authentication = new UserData(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole());

        MockHttpServletRequestBuilder httpRequest = get("/home").with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"));

        verify(userService).getById(user.getId());
    }

    @Test
    void getUnknownPage_whenNoResourceFoundExceptionOccurs_shouldReturnNotFoundErrorPage() throws Exception {

        User user = aRandomUser();

        UserData authentication = new UserData(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );

        MockHttpServletRequestBuilder httpRequest =
                get("/this-page-does-not-exist")
                        .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("error-page-not-found"));
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
}
