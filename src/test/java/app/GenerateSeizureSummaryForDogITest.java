package app;

import app.model.dto.dog.CreateNewDogRequest;
import app.model.dto.seizure.SeizureSummaryDto;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.dog.Dog;
import app.model.entity.dog.GenderDog;
import app.model.entity.seizure.Seizure;
import app.model.entity.seizure.SeizureRecovery;
import app.model.entity.seizure.SeizureSeverity;
import app.model.entity.user.User;
import app.repository.dog.DogRepository;
import app.repository.seizure.SeizureRepository;
import app.repository.user.UserRepository;
import app.service.dog.DogService;
import app.service.seizure.SeizureService;
import app.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GenerateSeizureSummaryForDogITest {

    @Autowired
    private UserService userService;
    @Autowired
    private DogService dogService;
    @Autowired
    private SeizureService seizureService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private SeizureRepository seizureRepository;

    @BeforeEach
    void cleanDatabase() {
        seizureRepository.deleteAll();
        dogRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void generateSeizureSummaryForDog_happyPath() {

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
                .dogPicture("https://www.testDogPicture.com")
                .gender(GenderDog.MALE)
                .dateOfBirth(LocalDate.of(2016, 6, 1))
                .food("testFood")
                .build();

        Dog dog = dogService.createDog(createNewDogRequest, registeredUser);

        assertNotNull(dog);
        assertNotNull(dog.getId());
        assertEquals("testDog", dog.getName());
        assertEquals(registeredUser.getId(), dog.getOwner().getId());

        LocalDate previousMonth = LocalDate.now().minusMonths(1);

        Seizure seizure1 = Seizure.builder()
                .date(previousMonth.withDayOfMonth(5))
                .time(LocalTime.of(10, 0))
                .duration(30)
                .severity(SeizureSeverity.MILD)
                .recovery(SeizureRecovery.FAST)
                .note("Test Seizure 1")
                .cluster(false)
                .dog(dog)
                .build();

        Seizure seizure2 = Seizure.builder()
                .date(previousMonth.withDayOfMonth(15))
                .time(LocalTime.of(14, 0))
                .duration(50)
                .severity(SeizureSeverity.MODERATE)
                .recovery(SeizureRecovery.NORMAL)
                .note("Test Seizure 2")
                .cluster(true)
                .dog(dog)
                .build();

        dog.setSeizures(new ArrayList<>());

        dog.getSeizures().add(seizure1);
        dog.getSeizures().add(seizure2);

        dogRepository.save(dog);

        SeizureSummaryDto seizureSummary = seizureService.generateSeizureSummaryForDog(dog);

        assertNotNull(seizureSummary);

        assertEquals(2, seizureSummary.getTotalSeizures());
        assertEquals(40, seizureSummary.getAverageDuration());
        assertEquals(50, seizureSummary.getLongestDuration());
        assertEquals(1, seizureSummary.getClusterSeizures());
        assertEquals(1, seizureSummary.getSeverityCount().get(SeizureSeverity.MILD));
        assertEquals(1, seizureSummary.getSeverityCount().get(SeizureSeverity.MODERATE));
    }
}