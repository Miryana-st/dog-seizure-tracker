package app.model.entity.dog;

import lombok.Getter;

@Getter
public enum GenderDog {
    FEMALE("Female"),
    MALE("Male");

    private final String displayGender;

    GenderDog(String displayGender) {
        this.displayGender = displayGender;
    }

}
