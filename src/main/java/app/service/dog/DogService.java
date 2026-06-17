package app.service.dog;

import app.model.dto.dog.CreateNewDogRequest;
import app.model.entity.dog.Dog;
import app.model.entity.user.User;
import app.repository.dog.DogRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DogService {

    DogRepository dogRepository;

    @Autowired
    public DogService(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    public void create(CreateNewDogRequest createNewDogRequest, User user) {

        Dog dog = Dog.builder()
                .owner(user)
                .name(createNewDogRequest.getName())
                .breed(createNewDogRequest.getBreed())
                .dogPicture(createNewDogRequest.getDogPicture())
                .food(createNewDogRequest.getFood())
                .dateOfBirth(createNewDogRequest.getDateOfBirth())
                .build();

        dogRepository.save(dog);
    }


    public List<Dog> getAllDogsByOwnerId(UUID ownerId) {
        return dogRepository.findAllByOwner_Id(ownerId);
    }
}
