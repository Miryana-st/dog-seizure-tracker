package app.service.dog;

import app.exception.NotFoundException;
import app.model.dto.dog.CreateNewDogRequest;
import app.model.dto.dog.EditDogRequest;
import app.model.entity.dog.Dog;
import app.model.entity.dog.GenderDog;
import app.model.entity.user.User;
import app.repository.dog.DogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static app.exception.ExceptionMessages.DOG_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DogServiceUTest {

    @Mock
    private DogRepository dogRepository;

    @InjectMocks
    private DogService dogService;

    @Test
    void whenCalculateDogAge_andDogHasDateOfBirth_thenReturnDogAge() {

        UUID dogId = UUID.randomUUID();

        Dog dogRetrievedFromDatabase = Dog.builder()
                .id(dogId)
                .dateOfBirth(LocalDate.now().minusYears(5))
                .build();

        when(dogRepository.findById(dogId)).thenReturn(Optional.of(dogRetrievedFromDatabase));

        Integer age = dogService.calculateDogAge(dogId);

        assertEquals(5, age);
    }

    @Test
    void whenCalculateDogAge_andDogDateOfBirthIsNull_thenReturnNull() {

        UUID dogId = UUID.randomUUID();

        Dog dogRetrievedFromDatabase = Dog.builder()
                .id(dogId)
                .dateOfBirth(null)
                .build();

        when(dogRepository.findById(dogId)).thenReturn(Optional.of(dogRetrievedFromDatabase));

        Integer age = dogService.calculateDogAge(dogId);

        assertNull(age);
    }

    @Test
    void whenCalculateDogAge_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID dogId = UUID.randomUUID();

        when(dogRepository.findById(dogId)).thenReturn(Optional.empty());

        NotFoundException exception =assertThrows(NotFoundException.class, () -> dogService.calculateDogAge(dogId));
        assertEquals(DOG_NOT_FOUND, exception.getMessage());

    }

    @Test
    void whenIsDogOwned_andRepositoryReturnsTrue_thenReturnTrue() {

        UUID userId = UUID.randomUUID();
        UUID dogId = UUID.randomUUID();

        User owner = User.builder()
                .id(userId)
                .build();

        Dog dogRetrievedFromDatabase = Dog.builder()
                .id(dogId)
                .owner(owner)
                .build();

        when(dogRepository.findById(dogId)).thenReturn(Optional.of(dogRetrievedFromDatabase));

        boolean result = dogService.isDogOwner(dogId, userId);

        assertTrue(result);
    }

    @Test
    void whenIsDogOwner_andDogBelongsToDifferentUser_thenReturnFalse() {

        UUID dogId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID loggedUserId = UUID.randomUUID();

        User owner = User.builder()
                .id(ownerId)
                .build();

        Dog dogRetrievedFromDatabase = Dog.builder()
                .id(dogId)
                .owner(owner)
                .build();

        when(dogRepository.findById(dogId)).thenReturn(Optional.of(dogRetrievedFromDatabase));

        boolean result = dogService.isDogOwner(dogId, loggedUserId);

        assertFalse(result);
    }

    @Test
    void whenIsDogOwner_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID dogId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(dogRepository.findById(dogId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> dogService.isDogOwner(dogId, userId));

        assertEquals(DOG_NOT_FOUND, exception.getMessage());
    }

    @Test
    void whenEditDogDetails_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID dogId = UUID.randomUUID();
        EditDogRequest dto = null;

        when(dogRepository.findById(dogId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> dogService.updateDogInformation(dogId, dto));

        assertEquals(DOG_NOT_FOUND, exception.getMessage());
    }

    @Test
    void whenEditDogDetails_andRepositoryReturnsDogFromTheDatabase_thenUpdateTheDogDetailsAndSaveUpdatedDog() {

        UUID dogId = UUID.randomUUID();
        EditDogRequest dto = EditDogRequest.builder()
                .name("Dog Name")
                .breed("Dog Breed")
                .dogPicture("www.dogPicture.com")
                .gender(GenderDog.MALE)
                .dateOfBirth(LocalDate.now().minusYears(5))
                .food("Food")
                .build();

        Dog dogRetrievedFromDatabase = Dog.builder()
                .id(dogId)
                .name("Old Dog Name")
                .breed("Old Dog Breed")
                .dogPicture("www.oldDogPicture.com")
                .gender(GenderDog.FEMALE)
                .dateOfBirth(LocalDate.now().minusYears(3))
                .food("Old Food")
                .build();

        when(dogRepository.findById(dogId)).thenReturn(Optional.of(dogRetrievedFromDatabase));

        dogService.updateDogInformation(dogId, dto);

        assertEquals("Dog Name", dogRetrievedFromDatabase.getName());
        assertEquals("Dog Breed", dogRetrievedFromDatabase.getBreed());
        assertEquals("www.dogPicture.com", dogRetrievedFromDatabase.getDogPicture());
        assertEquals(GenderDog.MALE, dogRetrievedFromDatabase.getGender());
        assertEquals(LocalDate.now().minusYears(5), dogRetrievedFromDatabase.getDateOfBirth());
        assertEquals("Food", dogRetrievedFromDatabase.getFood());
        verify(dogRepository).save(dogRetrievedFromDatabase);
    }

    @Test
    void whenCreateDog_thenCreateDogAndSaveIt() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        CreateNewDogRequest dto = CreateNewDogRequest.builder()
                .name("Max")
                .breed("Husky")
                .dogPicture("picture.jpg")
                .food("Food")
                .dateOfBirth(LocalDate.of(2020, 1, 1))
                .gender(GenderDog.MALE)
                .build();

        dogService.createDog(dto, user);

        ArgumentCaptor<Dog> dogCaptor = ArgumentCaptor.forClass(Dog.class);

        verify(dogRepository).save(dogCaptor.capture());

        Dog savedDog = dogCaptor.getValue();

        assertEquals(user.getId(), savedDog.getOwner().getId());
        assertEquals("Max", savedDog.getName());
        assertEquals("Husky", savedDog.getBreed());
        assertEquals("picture.jpg", savedDog.getDogPicture());
        assertEquals("Food", savedDog.getFood());
        assertEquals(LocalDate.of(2020, 1, 1), savedDog.getDateOfBirth());
        assertEquals(GenderDog.MALE, savedDog.getGender());
    }

    @Test
    void whenGetAllDogsByOwnerId_andRepositoryReturnsEmptyList_thenReturnEmptyList() {

        UUID ownerId = UUID.randomUUID();

        when(dogRepository.findAllByOwner_Id(ownerId)).thenReturn(Collections.emptyList());

        List<Dog> result = dogService.getAllDogsByOwnerId(ownerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(dogRepository).findAllByOwner_Id(ownerId);
    }

    @Test
    void whenGetAllDogsByOwnerId_andRepositoryReturnsDogs_thenReturnDogs() {

        UUID ownerId = UUID.randomUUID();

        List<Dog> dogs = List.of(
                Dog.builder()
                        .id(UUID.randomUUID())
                        .name("Max")
                        .breed("Husky")
                        .build(),
                Dog.builder()
                        .id(UUID.randomUUID())
                        .name("Luna")
                        .breed("Golden Retriever")
                        .build()
        );

        when(dogRepository.findAllByOwner_Id(ownerId)).thenReturn(dogs);

        List<Dog> result = dogService.getAllDogsByOwnerId(ownerId);

        assertEquals(2, result.size());
        assertEquals("Max", result.get(0).getName());
        assertEquals("Luna", result.get(1).getName());

        verify(dogRepository).findAllByOwner_Id(ownerId);
    }

    @Test
    void whenDeleteDogById_andRepositoryReturnsDog_thenRemoveDogFromOwnerAndDeleteDog() {

        UUID dogId = UUID.randomUUID();

        User owner = User.builder()
                .id(UUID.randomUUID())
                .dogs(new ArrayList<>())
                .build();

        Dog dogRetrievedFromDatabase = Dog.builder()
                .id(dogId)
                .name("Max")
                .owner(owner)
                .build();

        owner.getDogs().add(dogRetrievedFromDatabase);

        when(dogRepository.findById(dogId)).thenReturn(Optional.of(dogRetrievedFromDatabase));

        dogService.deleteDogById(dogId);

        assertTrue(owner.getDogs().isEmpty());

        verify(dogRepository).delete(dogRetrievedFromDatabase);
    }

    @Test
    void whenDeleteDogById_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID dogId = UUID.randomUUID();

        when(dogRepository.findById(dogId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> dogService.deleteDogById(dogId));

        assertEquals(DOG_NOT_FOUND, exception.getMessage());
    }
}
