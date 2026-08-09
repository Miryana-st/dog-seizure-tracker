package app.web.seizure;

import app.model.entity.dog.Dog;
import app.model.entity.dog.GenderDog;
import app.model.entity.seizure.Seizure;
import app.model.entity.seizure.SeizureRecovery;
import app.model.entity.seizure.SeizureSeverity;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.security.user.UserData;
import app.service.dog.DogService;
import app.service.pdf.PdfService;
import app.service.seizure.SeizureService;
import app.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeizureController.class)
public class SeizureControllerApiTest {

    @MockitoBean(name = "userService")
    private UserService userService;
    @MockitoBean(name = "dogService")
    private DogService dogService;
    @MockitoBean
    private SeizureService seizureService;
    @MockitoBean
    private PdfService pdfService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getSeizuresForDogEndpoint_whenAuthenticatedUser_shouldReturnSeizuresView() throws Exception {

        User owner = aRandomUser();

        UserData authentication = new UserData(
                owner.getId(),
                owner.getUsername(),
                owner.getPassword(),
                owner.getRole());

        Dog dog = aRandomDog();
        dog.setOwner(owner);

        Seizure seizure = aRandomSeizure();
        List<Seizure> seizures = List.of(seizure);

        dog.setSeizures(seizures);

        when(dogService.isDogOwner(dog.getId(), authentication.getUserId())).thenReturn(true);
        when(seizureService.getAllSeizuresByDog_IdOrderByDateDescTimeDesc(dog.getId())).thenReturn(seizures);
        when(dogService.getDogById(dog.getId())).thenReturn(dog);

        MockHttpServletRequestBuilder httpRequest = get("/dogs/{dogId}/seizures", dog.getId())
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("seizures"))
                .andExpect(model().attribute("dog", dog))
                .andExpect(model().attribute("seizures", seizures));

        verify(dogService).isDogOwner(dog.getId(), authentication.getUserId());
        verify(seizureService).getAllSeizuresByDog_IdOrderByDateDescTimeDesc(dog.getId());
    }

    @Test
    void getNewSeizureEndpoint_whenAuthenticatedUser_shouldReturnAddSeizureView() throws Exception {

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

        MockHttpServletRequestBuilder httpRequest = get("/dogs/{dogId}/seizures/new", dog.getId())
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("add-seizure"))
                .andExpect(model().attribute("dog", dog))
                .andExpect(model().attributeExists("createNewSeizureRequest"));

        verify(dogService).isDogOwner(dog.getId(), authentication.getUserId());
    }

    @Test
    void getSeizureLogEndpoint_whenAuthenticatedUser_shouldReturnSeizureLogView() throws Exception {

        User owner = aRandomUser();

        UserData authentication = new UserData(
                owner.getId(),
                owner.getUsername(),
                owner.getPassword(),
                owner.getRole());

        Dog dog = aRandomDog();
        dog.setOwner(owner);

        Seizure seizure = aRandomSeizure();
        seizure.setDog(dog);

        when(dogService.isDogOwner(dog.getId(), authentication.getUserId())).thenReturn(true);
        when(dogService.getDogById(dog.getId())).thenReturn(dog);
        when(seizureService.getSeizureById(seizure.getId())).thenReturn(seizure);

        MockHttpServletRequestBuilder httpRequest = get("/dogs/{dogId}/seizures/{seizureId}/details", dog.getId(), seizure.getId())
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("seizure-profile"))
                .andExpect(model().attribute("dog", dog))
                .andExpect(model().attribute("seizure", seizure))
                .andExpect(model().attributeExists("editSeizureRequest"));

        verify(dogService).isDogOwner(dog.getId(), authentication.getUserId());
        verify(seizureService).getSeizureById(seizure.getId());
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
                .seizures(new ArrayList<>())
                .build();
    }

    public static Seizure aRandomSeizure() {

        return Seizure.builder()
                .id(UUID.randomUUID())
                .date(LocalDate.of(2020, 1, 1))
                .time(LocalTime.of(12, 0))
                .duration(30)
                .severity(SeizureSeverity.MILD)
                .recovery(SeizureRecovery.FAST)
                .note("Test Seizure Note")
                .cluster(false)
                .build();
    }
}

