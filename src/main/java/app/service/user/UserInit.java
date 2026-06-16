package app.service.user;

import app.model.dto.UserRegisterRequest;
import app.model.entity.user.User;
import app.property.user.UserProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserInit implements ApplicationRunner {

    private final UserService userService;
    private final UserProperties userProperties;

    @Autowired
    public UserInit(UserService userService, UserProperties userProperties) {
        this.userService = userService;
        this.userProperties = userProperties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<User> users = userService.getAllUsers();

        boolean defaultUserDoesNotExist = users.stream().noneMatch(user -> user.getUsername().equals(userProperties.getDefaultUser().getUsername()));

        if (defaultUserDoesNotExist) {

            UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                    .username(userProperties.getDefaultUser().getUsername())
                    .password(userProperties.getDefaultUser().getPassword())
                    .firstName(userProperties.getDefaultUser().getFirstName())
                    .lastName(userProperties.getDefaultUser().getLastName())
                    .email(userProperties.getDefaultUser().getEmail())
                    .build();

            userService.register(userRegisterRequest);
        }
    }
}