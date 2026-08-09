package app;

import app.model.dto.dog.CreateNewDogRequest;
import app.model.dto.medication.Dosage;
import app.model.dto.medication.MedicationResponse;
import app.model.dto.medication.MedicationScheduleResponse;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.dog.Dog;
import app.model.entity.dog.GenderDog;
import app.model.entity.user.User;
import app.repository.dog.DogRepository;
import app.repository.user.UserRepository;
import app.service.dog.DogService;
import app.service.medication.MedicationScheduleService;
import app.service.medication.MedicationService;
import app.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class AddMedicationScheduleITest {

    @Autowired
    private UserService userService;
    @Autowired
    private DogService dogService;
    @Autowired
    private MedicationService medicationService;
    @Autowired
    private MedicationScheduleService medicationScheduleService;

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
    void addMedicationSchedule_happyPath() {

        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .username("testUserSchedule")
                .password("123456789")
                .email("testUserSchedule@example.com")
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

        medicationService.addMedication(
                dog.getId(),
                "testMedication",
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 1, 10),
                BigDecimal.valueOf(50.5));

        List<MedicationResponse> medications = medicationService.getMedicationsByDogId(dog.getId());

        assertNotNull(medications);
        assertEquals(1, medications.size());

        MedicationResponse medication = medications.getFirst();

        assertNotNull(medication.getId());

        LocalTime administrationTime = LocalTime.of(8, 30);
        BigDecimal amount = BigDecimal.valueOf(2.5);
        Dosage dosage = Dosage.TABLET;

        medicationScheduleService.addMedicationSchedule(
                dog.getId(),
                medication.getId(),
                administrationTime,
                amount,
                dosage);

        List<MedicationScheduleResponse> schedules = medicationScheduleService.getMedicationSchedulesByDogId(dog.getId());

        assertNotNull(schedules);
        assertEquals(1, schedules.size());

        MedicationScheduleResponse schedule = schedules.getFirst();

        assertNotNull(schedule);
        assertNotNull(schedule.getId());
        assertEquals(medication.getId(), schedule.getMedicationId());
        assertEquals(administrationTime, schedule.getAdministrationTime());
        assertEquals(0, schedule.getAmount().compareTo(amount));
        assertEquals(dosage, schedule.getDosage());
    }
}
