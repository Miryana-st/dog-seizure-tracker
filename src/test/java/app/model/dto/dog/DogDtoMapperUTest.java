package app.model.dto.dog;

import app.model.entity.dog.Dog;
import app.model.entity.dog.GenderDog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DogDtoMapperUTest {

    @Test
    void fromDogToEditDogRequest_whenDogWithDetailsIsPassed_thenDtoIsReturnedWithSameDetails() {

        Dog dog = Dog.builder()
                .name("testName")
                .breed("testBreed")
                .dogPicture("https://www.test.com")
                .gender(GenderDog.MALE)
                .food("testFood")
                .dateOfBirth(LocalDate.of(2020, 1, 1))
                .build();

        EditDogRequest result = DogDtoMapper.fromDog(dog);

        assertEquals("testName", result.getName());
        assertEquals("testBreed", result.getBreed());
        assertEquals("https://www.test.com", result.getDogPicture());
        assertEquals(GenderDog.MALE, result.getGender());
        assertEquals("testFood", result.getFood());
        assertEquals(LocalDate.of(2020, 1, 1), result.getDateOfBirth());
    }
}
