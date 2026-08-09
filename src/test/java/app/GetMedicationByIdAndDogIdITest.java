package app;

import app.model.dto.dog.CreateNewDogRequest;
import app.model.dto.medication.MedicationResponse;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.dog.Dog;
import app.model.entity.dog.GenderDog;
import app.model.entity.user.User;
import app.repository.dog.DogRepository;
import app.repository.user.UserRepository;
import app.service.dog.DogService;
import app.service.medication.MedicationService;
import app.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GetMedicationByIdAndDogIdITest {

    @Autowired
    private UserService userService;
    @Autowired
    private DogService dogService;
    @Autowired
    private MedicationService medicationService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DogRepository dogRepository;

    @BeforeEach
    void cleanDatabase() {
        dogRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getMedicationByIdAndDogId_happyPath() {

        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .username("testUser")
                .password("123456789")
                .email("testUser@example.com")
                .build();

        User registeredUser = userService.registerUser(userRegisterRequest);

        assertNotNull(registeredUser);
        assertNotNull(registeredUser.getId());

        CreateNewDogRequest createNewDogRequest = CreateNewDogRequest.builder()
                .name("testDog")
                .breed("testBreed")
                .dogPicture("https://www.test.com")
                .gender(GenderDog.MALE)
                .dateOfBirth(LocalDate.of(2016, 6, 1))
                .food("testFood")
                .build();

        Dog dog = dogService.createDog(createNewDogRequest, registeredUser);

        assertNotNull(dog);
        assertNotNull(dog.getId());
        assertEquals("testDog", dog.getName());
        assertEquals(registeredUser.getId(), dog.getOwner().getId());

        medicationService.addMedication(
                dog.getId(),
                "testMedication",
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 1, 10),
                BigDecimal.valueOf(50.5));

        List<MedicationResponse> medications = medicationService.getMedicationsByDogId(dog.getId());

        assertNotNull(medications);
        assertEquals(1, medications.size());

        MedicationResponse addedMedication = medications.getFirst();

        assertNotNull(addedMedication.getId());

        MedicationResponse medication = medicationService.getMedicationByIdAndDogId(
                        addedMedication.getId(),
                        dog.getId());

        assertNotNull(medication);
        assertEquals(addedMedication.getId(), medication.getId());
        assertEquals(dog.getId(), medication.getDogId());
        assertEquals("testMedication", medication.getName());
        assertEquals(LocalDate.of(2020, 1, 1), medication.getStartDate());
        assertEquals(LocalDate.of(2020, 1, 10), medication.getEndDate());
        assertEquals(0, medication.getMedicationConcentrationMg().compareTo(BigDecimal.valueOf(50.5)));
    }
}
