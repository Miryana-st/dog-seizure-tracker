package app.web.medication;

import app.exception.MedicationMicroserviceUnavailableException;
import app.model.dto.medication.MedicationRequest;
import app.model.dto.medication.MedicationResponse;
import app.model.entity.dog.Dog;
import app.model.entity.dog.GenderDog;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.security.user.UserData;
import app.service.dog.DogService;
import app.service.medication.MedicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicationController.class)
public class MedicationControllerApiTest {

    @MockitoBean(name = "dogService")
    private DogService dogService;
    @MockitoBean(name = "medicationService")
    private MedicationService medicationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getMedicationSelectionEndpoint_whenAuthenticatedUser_shouldReturnMedicationSelectionViewWithUsersDogsInSelector() throws Exception {

        User owner = aRandomUser();

        UserData authentication = new UserData(
                owner.getId(),
                owner.getUsername(),
                owner.getPassword(),
                owner.getRole());

        Dog dog = aRandomDog();
        dog.setOwner(owner);

        List<Dog> dogs = List.of(dog);

        when(dogService.getAllDogsByOwnerId(authentication.getUserId())).thenReturn(dogs);

        MockHttpServletRequestBuilder httpRequest =
                get("/medications")
                        .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("medication"))
                .andExpect(model().attribute("dogs", dogs))
                .andExpect(model().attribute("dogMedications", Collections.emptyList()));

        verify(dogService).getAllDogsByOwnerId(authentication.getUserId());
    }

    @Test
    void getMedicationsEndpoint_whenAuthenticatedUserAndSelectedDog_shouldReturnMedicationsViewForSelectedDog() throws Exception {

        User owner = aRandomUser();

        UserData authentication = new UserData(
                owner.getId(),
                owner.getUsername(),
                owner.getPassword(),
                owner.getRole());

        Dog dog = aRandomDog();
        dog.setOwner(owner);

        List<Dog> dogs = List.of(dog);

        MedicationResponse medication = MedicationResponse.builder()
                .id(UUID.randomUUID())
                .dogId(dog.getId())
                .name("Test Medication")
                .startDate(LocalDate.of(2024, 6, 1))
                .endDate(LocalDate.of(2024, 6, 30))
                .medicationConcentrationMg(BigDecimal.valueOf(50.5))
                .build();

        List<MedicationResponse> medications = List.of(medication);

        when(dogService.isDogOwner(dog.getId(), authentication.getUserId())).thenReturn(true);
        when(dogService.getAllDogsByOwnerId(authentication.getUserId())).thenReturn(dogs);
        when(dogService.getDogById(dog.getId())).thenReturn(dog);
        when(dogService.calculateDogAge(dog.getId())).thenReturn(6);
        when(medicationService.getMedicationsByDogId(dog.getId())).thenReturn(medications);

        MockHttpServletRequestBuilder httpRequest = get("/medications/{dogId}", dog.getId())
                .with(user(authentication));

        Integer dogAge = LocalDate.now().getYear() - dog.getDateOfBirth().getYear();

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("medication"))
                .andExpect(model().attribute("dogs", dogs))
                .andExpect(model().attribute("selectedDogId", dog.getId()))
                .andExpect(model().attribute("dog", dog))
                .andExpect(model().attribute("dogAge", dogAge))
                .andExpect(model().attribute("dogMedications", medications));

        verify(dogService).isDogOwner(dog.getId(), authentication.getUserId());
        verify(dogService).getAllDogsByOwnerId(authentication.getUserId());
        verify(dogService).getDogById(dog.getId());
        verify(dogService).calculateDogAge(dog.getId());
        verify(medicationService).getMedicationsByDogId(dog.getId());
    }

    @Test
    void getMedicationsEndpoint_whenMedicationMicroserviceUnavailable_shouldReturnErrorPage() throws Exception {

        User owner = aRandomUser();

        UserData authentication = new UserData(
                owner.getId(),
                owner.getUsername(),
                owner.getPassword(),
                owner.getRole()
        );

        Dog dog = aRandomDog();
        dog.setOwner(owner);

        when(dogService.isDogOwner(dog.getId(), authentication.getUserId())).thenReturn(true);
        when(dogService.getAllDogsByOwnerId(authentication.getUserId())).thenReturn(List.of(dog));
        when(dogService.getDogById(dog.getId())).thenReturn(dog);
        when(dogService.calculateDogAge(dog.getId())).thenReturn(6);
        when(medicationService.getMedicationsByDogId(dog.getId())).thenThrow(MedicationMicroserviceUnavailableException.class);

        MockHttpServletRequestBuilder httpRequest =
                get("/medications/{dogId}", dog.getId())
                        .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().is5xxServerError())
                .andExpect(view().name("error-page"));
    }

    @Test
    void getAddMedicationEndpoint_whenAuthenticatedUser_shouldReturnAddMedicationView() throws Exception {

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

        MockHttpServletRequestBuilder httpRequest =
                get("/medications/{dogId}/new", dog.getId())
                        .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("add-medication"))
                .andExpect(model().attribute("dog", dog))
                .andExpect(model().attributeExists("addMedicationRequest"));

        verify(dogService).isDogOwner(dog.getId(), authentication.getUserId());
        verify(dogService).getDogById(dog.getId());
    }

    @Test
    void getEditMedicationEndpoint_whenAuthenticatedUser_shouldReturnMedicationProfileView() throws Exception {

        User owner = aRandomUser();

        UserData authentication = new UserData(
                owner.getId(),
                owner.getUsername(),
                owner.getPassword(),
                owner.getRole());

        Dog dog = aRandomDog();
        dog.setOwner(owner);

        UUID medicationId = UUID.randomUUID();

        MedicationResponse medication = MedicationResponse.builder()
                .id(medicationId)
                .dogId(dog.getId())
                .name("Test Medication")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 1, 10))
                .medicationConcentrationMg(BigDecimal.valueOf(50.5))
                .build();

        when(dogService.isDogOwner(dog.getId(), authentication.getUserId())).thenReturn(true);
        when(medicationService.getMedicationByIdAndDogId(medicationId, dog.getId())).thenReturn(medication);
        when(dogService.getDogById(dog.getId())).thenReturn(dog);

        MockHttpServletRequestBuilder httpRequest =
                get("/medications/{dogId}/{medicationId}/details",
                        dog.getId(), medicationId)
                        .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("medication-profile"))
                .andExpect(model().attribute("dog", dog))
                .andExpect(model().attribute("medication", medication))
                .andExpect(model().attributeExists("editMedicationRequest"));

        verify(dogService).isDogOwner(dog.getId(), authentication.getUserId());
        verify(medicationService).getMedicationByIdAndDogId(medicationId, dog.getId());
        verify(dogService).getDogById(dog.getId());
    }

    @Test
    void addMedicationEndpoint_whenValidRequest_shouldRedirectToMedicationsPage() throws Exception {

        User owner = aRandomUser();

        UserData authentication = new UserData(
                owner.getId(),
                owner.getUsername(),
                owner.getPassword(),
                owner.getRole());

        Dog dog = aRandomDog();
        dog.setOwner(owner);

        MedicationRequest medicationRequest = MedicationRequest.builder()
                .name("Test Medication")
                .startDate(LocalDate.of(2026, 8, 1))
                .endDate(LocalDate.of(2026, 8, 31))
                .medicationConcentrationMg(BigDecimal.valueOf(50.5))
                .build();

        when(dogService.isDogOwner(dog.getId(), authentication.getUserId()))
                .thenReturn(true);

        when(dogService.getDogById(dog.getId()))
                .thenReturn(dog);

        MockHttpServletRequestBuilder httpRequest =
                post("/medications/{dogId}/new", dog.getId())
                        .with(user(authentication))
                        .with(csrf())
                        .param("name", medicationRequest.getName())
                        .param("startDate", medicationRequest.getStartDate().toString())
                        .param("endDate", medicationRequest.getEndDate().toString())
                        .param("medicationConcentrationMg", medicationRequest.getMedicationConcentrationMg().toString());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medications/" + dog.getId()));

        verify(dogService).isDogOwner(dog.getId(), authentication.getUserId());
        verify(dogService).getDogById(dog.getId());
        verify(medicationService).addMedication(
                eq(dog.getId()),
                eq(medicationRequest.getName()),
                eq(medicationRequest.getStartDate()),
                eq(medicationRequest.getEndDate()),
                eq(medicationRequest.getMedicationConcentrationMg()));
    }

    @Test
    void addMedicationEndpoint_whenInvalidRequest_shouldReturnAddMedicationViewWithErrors() throws Exception {

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

        MockHttpServletRequestBuilder httpRequest =
                post("/medications/{dogId}/new", dog.getId())
                        .with(user(authentication))
                        .with(csrf())
                        .param("name", "")
                        .param("startDate", "")
                        .param("endDate", "")
                        .param("medicationConcentrationMg", "");

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("add-medication"))
                .andExpect(model().attribute("dog", dog))
                .andExpect(model().attributeExists("addMedicationRequest"))
                .andExpect(model().attributeHasFieldErrors(
                        "addMedicationRequest",
                        "name",
                        "startDate",
                        "medicationConcentrationMg"));

        verify(dogService).isDogOwner(dog.getId(), authentication.getUserId());
        verify(dogService).getDogById(dog.getId());
        verifyNoInteractions(medicationService);
    }

    @Test
    void deleteMedicationEndpoint_whenAuthenticatedUser_shouldRedirectToMedicationsPage() throws Exception {

        User owner = aRandomUser();

        UserData authentication = new UserData(
                owner.getId(),
                owner.getUsername(),
                owner.getPassword(),
                owner.getRole());

        Dog dog = aRandomDog();
        dog.setOwner(owner);

        UUID medicationId = UUID.randomUUID();

        when(dogService.isDogOwner(dog.getId(), authentication.getUserId())).thenReturn(true);

        MockHttpServletRequestBuilder httpRequest =
                delete("/medications/{dogId}/{medicationId}",
                        dog.getId(), medicationId)
                        .with(user(authentication))
                        .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medications/" + dog.getId()));

        verify(dogService).isDogOwner(dog.getId(), authentication.getUserId());
        verify(medicationService).deleteMedication(medicationId, dog.getId());
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
                .dogPicture("https://www.testDogPicture.com")
                .gender(GenderDog.MALE)
                .dateOfBirth(LocalDate.of(2020, 1, 1))
                .food("Test Dog Food")
                .seizures(new ArrayList<>())
                .build();
    }
}
