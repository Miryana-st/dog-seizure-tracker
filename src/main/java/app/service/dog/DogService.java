package app.service.dog;

import app.exception.NotFoundException;
import app.model.dto.dog.CreateNewDogRequest;
import app.model.dto.dog.EditDogRequest;
import app.model.entity.dog.Dog;
import app.model.entity.user.User;
import app.repository.dog.DogRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import static app.exception.ExceptionMessages.DOG_NOT_FOUND;

@Service
public class DogService {

    private final DogRepository dogRepository;

    @Autowired
    public DogService(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    @Transactional
    public void createDog(CreateNewDogRequest createNewDogRequest, User user) {

        Dog dog = Dog.builder()
                .owner(user)
                .name(createNewDogRequest.getName())
                .breed(createNewDogRequest.getBreed())
                .dogPicture(createNewDogRequest.getDogPicture())
                .food(createNewDogRequest.getFood())
                .dateOfBirth(createNewDogRequest.getDateOfBirth())
                .gender(createNewDogRequest.getGender())
                .build();

        dogRepository.save(dog);
    }

    public List<Dog> getAllDogsByOwnerId(UUID ownerId) {
        return dogRepository.findAllByOwner_Id(ownerId);
    }

    public Dog getDogById(UUID dogId) {
        return dogRepository.findById(dogId)
                .orElseThrow(() -> new NotFoundException(DOG_NOT_FOUND));
    }

    public boolean isDogOwner(UUID dogId, UUID userId) {
        Dog dog = getDogById(dogId);
        return dog.getOwner().getId().equals(userId);
    }

    @Transactional
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
    }

    @Transactional
    public void deleteDogById(UUID id) {

        Dog dogToDelete = dogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DOG_NOT_FOUND));

        dogToDelete.getOwner().getDogs().remove(dogToDelete);

        dogRepository.delete(dogToDelete);
    }

    public Integer calculateDogAge(UUID dogId) {

        Dog dog = getDogById(dogId);

        if (dog.getDateOfBirth() == null) {
            return null;
        }

        return Period.between(
                dog.getDateOfBirth(),
                LocalDate.now()
        ).getYears();
    }
}
