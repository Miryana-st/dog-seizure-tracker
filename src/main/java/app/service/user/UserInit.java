//package app.service.user;
//
//import app.model.dto.user.UserDto;
//import app.model.dto.user.UserRegisterRequest;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class UserInit implements CommandLineRunner {
//
//    private final UserService userService;
//
//    public UserInit(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        List<UserDto> users = userService.findAll();
//        if (!users.isEmpty()) {
//            return;
//        }
//
//        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
//                .username("defaultUser")
//                .password("defaultPassword")
//                .firstName("Default First Name")
//                .lastName("Default Last Name")
//                .email("Default@default.com")
//                .build();
//
//        userService.registerAdmin(userRegisterRequest);
//    }
//}
