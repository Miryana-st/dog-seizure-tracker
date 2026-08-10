package app.service.dog;

import app.exception.NotFoundException;
import app.model.dto.dog.CreateNewDogRequest;
import app.model.dto.dog.EditDogRequest;
import app.model.entity.dog.Dog;
import app.model.entity.user.User;
import app.repository.dog.DogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import static app.exception.ExceptionMessages.DOG_NOT_FOUND;

@Slf4j
@Service
public class DogService {

    private final DogRepository dogRepository;

    @Autowired
    public DogService(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "dogsByOwnerId", allEntries = true),
            @CacheEvict(value = "allDogs", allEntries = true)
    })
    public Dog createDog(CreateNewDogRequest createNewDogRequest, User user) {

        Dog dog = Dog.builder()
                .owner(user)
                .name(createNewDogRequest.getName())
                .breed(createNewDogRequest.getBreed())
                .dogPicture(createNewDogRequest.getDogPicture())
                .food(createNewDogRequest.getFood())
                .dateOfBirth(createNewDogRequest.getDateOfBirth())
                .gender(createNewDogRequest.getGender())
                .build();

        Dog save = dogRepository.save(dog);
        log.info("Created dog '{}' for user with id: {}", createNewDogRequest.getName(), user.getId());

        return save;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "dogById", key = "#id"),
            @CacheEvict(value = "dogAgeById", key = "#id"),
            @CacheEvict(value = "dogsByOwnerId", allEntries = true),
            @CacheEvict(value = "allDogs", allEntries = true)
    })
    public void updateDogInformation(UUID id, EditDogRequest editDogRequest) {
        Dog dog = dogRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException(DOG_NOT_FOUND));

        dog.setName(editDogRequest.getName());
        dog.setBreed(editDogRequest.getBreed());
        dog.setDogPicture(editDogRequest.getDogPicture());
        dog.setGender(editDogRequest.getGender());
        dog.setFood(editDogRequest.getFood());
        dog.setDateOfBirth(editDogRequest.getDateOfBirth());

        dogRepository.save(dog);
        log.info("Updated dog with id: {}", id);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "dogById", key = "#id"),
            @CacheEvict(value = "dogAgeById", key = "#id"),
            @CacheEvict(value = "dogsByOwnerId", allEntries = true),
            @CacheEvict(value = "allDogs", allEntries = true)
    })
    public void deleteDogById(UUID id) {

        Dog dogToDelete = dogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DOG_NOT_FOUND));

        dogToDelete.getOwner().getDogs().remove(dogToDelete);

        dogRepository.delete(dogToDelete);
        log.info("Deleted dog with id: {}", id);
    }

    public boolean isDogOwner(UUID dogId, UUID userId) {

        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new NotFoundException(DOG_NOT_FOUND));

        return dog.getOwner().getId().equals(userId);
    }

    @Cacheable(value = "dogAgeById", key = "#dogId")
    public Integer calculateDogAge(UUID dogId) {

        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new NotFoundException(DOG_NOT_FOUND));

        if (dog.getDateOfBirth() == null) {
            return null;
        }

        return Period.between(
                dog.getDateOfBirth(),
                LocalDate.now()
        ).getYears();
    }

    @Cacheable(value = "dogsByOwnerId", key = "#ownerId")
    public List<Dog> getAllDogsByOwnerId(UUID ownerId) {

        return dogRepository.findAllByOwner_Id(ownerId);
    }

    @Cacheable(value = "dogById", key = "#dogId")
    public Dog getDogById(UUID dogId) {

        return dogRepository.findById(dogId).orElseThrow(() -> new NotFoundException(DOG_NOT_FOUND));
    }

    @Cacheable("allDogs")
    public List<Dog> getAllDogs() {
        return dogRepository.findAll();
    }
}
