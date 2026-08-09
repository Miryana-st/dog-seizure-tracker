package app;

import app.model.dto.dog.CreateNewDogRequest;
import app.model.dto.seizure.CreateNewSeizureRequest;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.dog.Dog;
import app.model.entity.dog.GenderDog;
import app.model.entity.seizure.Seizure;
import app.model.entity.seizure.SeizureRecovery;
import app.model.entity.seizure.SeizureSeverity;
import app.model.entity.user.User;
import app.repository.seizure.SeizureRepository;
import app.service.dog.DogService;
import app.service.seizure.SeizureService;
import app.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CreateSeizureEntryITest {
    @Autowired
    private UserService userService;
    @Autowired
    private DogService dogService;
    @Autowired
    private SeizureService seizureService;
    @Autowired
    private SeizureRepository seizureRepository;

    @Test
    void createSeizureEntry_happyPath() {

        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .username("testUser2")
                .password("123456789")
                .email("testUser2@example.com")
                .build();

        User registeredUser = userService.registerUser(userRegisterRequest);

        assertNotNull(registeredUser);
        assertNotNull(registeredUser.getId());

        CreateNewDogRequest createNewDogRequest = CreateNewDogRequest.builder()
                .name("testDog")
                .breed("testBreed")
                .dogPicture("www.test.com")
                .gender(GenderDog.MALE)
                .dateOfBirth(LocalDate.of(2016, 6, 1))
                .food("testFood")
                .build();

        Dog dog = dogService.createDog(createNewDogRequest, registeredUser);

        assertNotNull(dog);
        assertNotNull(dog.getId());

        CreateNewSeizureRequest seizureRequest = CreateNewSeizureRequest.builder()
                .date(LocalDate.of(2022, 5, 1))
                .time(LocalTime.of(22, 30))
                .duration(20)
                .note("Some text")
                .cluster(false)
                .severity(SeizureSeverity.MILD)
                .recovery(SeizureRecovery.FAST)
                .build();

        Seizure seizure = seizureService.createSeizureEntry(seizureRequest, dog);

        assertNotNull(seizure);
        assertNotNull(seizure.getId());
        assertEquals(seizureRequest.getDate(), seizure.getDate());
        assertEquals(seizureRequest.getTime(), seizure.getTime());
        assertEquals(seizureRequest.getDuration(), seizure.getDuration());
        assertEquals(seizureRequest.getNote(), seizure.getNote());
        assertEquals(seizureRequest.isCluster(), seizure.isCluster());
        assertEquals(seizureRequest.getSeverity(), seizure.getSeverity());
        assertEquals(seizureRequest.getRecovery(), seizure.getRecovery());

        Seizure seizureFromDatabase = seizureRepository.findById(seizure.getId())
                .orElseThrow();

        assertEquals(seizure.getId(), seizureFromDatabase.getId());
        assertEquals(dog.getId(), seizureFromDatabase.getDog().getId());
        assertEquals(seizureRequest.getDate(), seizureFromDatabase.getDate());
        assertEquals(seizureRequest.getTime(), seizureFromDatabase.getTime());
        assertEquals(seizureRequest.getDuration(), seizureFromDatabase.getDuration());
        assertEquals(seizureRequest.getNote(), seizureFromDatabase.getNote());
        assertEquals(seizureRequest.isCluster(), seizureFromDatabase.isCluster());
        assertEquals(seizureRequest.getSeverity(), seizureFromDatabase.getSeverity());
        assertEquals(seizureRequest.getRecovery(), seizureFromDatabase.getRecovery());
    }
}
