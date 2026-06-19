package app.service.user;

import app.model.dto.user.UserEditRequest;
import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserRegisterRequest userRegisterRequest) {

        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(userRegisterRequest.getUsername(), userRegisterRequest.getEmail());

        if (optionalUser.isPresent()) {
            throw new RuntimeException("User with this email or username already exists.");
        }

        User user = User.builder()
                .username(userRegisterRequest.getUsername())
                .firstName(userRegisterRequest.getFirstName())
                .lastName(userRegisterRequest.getLastName())
                .email(userRegisterRequest.getEmail())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    public User loginUser(UserLoginRequest userLoginRequest) {

        Optional<User> optionalUser = userRepository.findByUsername(userLoginRequest.getUsername());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Incorrect username or password.");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect username or password.");
        }

        return user;
    }

    public void updateUser(UUID id, UserEditRequest userEditRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("User with id [%s] not found!".formatted(id)));

        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setEmail(userEditRequest.getEmail());
        user.setPhoneNumber(userEditRequest.getPhoneNumber());

        userRepository.save(user);
    }


    public void switchRole(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("User with id [%s] not found!".formatted(id)));

        user.setRole(user.getRole() == UserRole.USER ? UserRole.ADMIN : UserRole.USER);
        userRepository.save(user);
    }

    public User getById(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id [%s] not found!".formatted(userId)));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
