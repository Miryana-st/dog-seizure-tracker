package app.model.dto.dog;

import app.model.entity.dog.Dog;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DogDtoMapper {

    public static EditDogRequest fromDog(Dog dog) {

        return EditDogRequest.builder()
                .name(dog.getName())
                .breed(dog.getBreed())
                .dogPicture(dog.getDogPicture())
                .gender(dog.getGender())
                .food(dog.getFood())
                .dateOfBirth(dog.getDateOfBirth())
                .build();
    }
}
