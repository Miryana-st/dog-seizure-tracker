package app.web.dog;

import app.model.entity.dog.Dog;
import app.model.entity.dog.GenderDog;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.security.user.UserData;
import app.service.dog.DogService;
import app.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DogController.class)
public class DogControllerApiTest {

    @MockitoBean(name = "userService")
    private UserService userService;
    @MockitoBean(name = "dogService")
    private DogService dogService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUserDogsEndpoint_shouldReturnDogsViewAndDogsForAuthenticatedUser() throws Exception {

        User owner = aRandomUser();

        UserData authentication = new UserData(
                owner.getId(),
                owner.getUsername(),
                owner.getPassword(),
                owner.getRole());

        Dog dog = aRandomDog();
        dog.setOwner(owner);

        when(dogService.getAllDogsByOwnerId(owner.getId())).thenReturn(List.of(dog));

        MockHttpServletRequestBuilder httpRequest = get("/dogs")
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("dogs"))
                .andExpect(model().attribute("dogs", List.of(dog)));

        verify(dogService).getAllDogsByOwnerId(authentication.getUserId());
    }

    @Test
    void getNewDogEndpoint_whenUserIsAuthenticated_shouldReturnAddNewDogView() throws Exception {

        User owner = aRandomUser();

        UserData authentication = new UserData(
                owner.getId(),
                owner.getUsername(),
                owner.getPassword(),
                owner.getRole());

        MockHttpServletRequestBuilder httpRequest = get("/dogs/new")
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("add-dog"))
                .andExpect(model().attributeExists("createNewDogRequest"));
    }

    @Test
    void getDogProfileEndpoint_whenUserIsAuthenticated_shouldReturnDogProfileView() throws Exception {

        User owner = aRandomUser();

        UserData authentication = new UserData(
                owner.getId(),
                owner.getUsername(),
                owner.getPassword(),
                owner.getRole());

        Dog dog = aRandomDog();
        dog.setOwner(owner);

        when(dogService.isDogOwner(dog.getId(), authentication.getUserId())).thenReturn(true);
        when(dogService.getDogById(dog.getId())).thenReturn(dog);

        MockHttpServletRequestBuilder httpRequest = get("/dogs/{id}/details", dog.getId())
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("dog-profile"))
                .andExpect(model().attributeExists("editDogRequest"));

        verify(dogService).isDogOwner(dog.getId(), authentication.getUserId());
        verify(dogService).getDogById(dog.getId());
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

    public static Dog aRandomDog() {

        return Dog.builder()
                .id(UUID.randomUUID())
                .name("Test Dog")
                .breed("Husky")
                .dogPicture("www.testDogPicture.com")
                .gender(GenderDog.MALE)
                .dateOfBirth(LocalDate.of(2020, 1, 1))
                .food("Test Dog Food")
                .build();
    }
}
