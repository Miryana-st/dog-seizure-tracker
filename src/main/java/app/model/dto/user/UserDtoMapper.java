package app.model.dto.user;

import app.model.entity.user.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserDtoMapper {

    public static UserEditRequest fromUser(User user) {

        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
