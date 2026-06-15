package app.service.user;

import app.model.dto.UserLoginRequest;
import app.model.dto.UserRegisterRequest;
import app.model.entity.user.User;
import app.repository.user.UserRepository;
import app.service.dog.DogService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DogService dogService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, DogService dogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.dogService = dogService;
    }

    public void register(UserRegisterRequest userRegisterRequest) {

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
                .build();

        userRepository.save(user);
    }

    public User login(UserLoginRequest userLoginRequest) {

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

    public User getById(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id [%s] not found!".formatted(userId)));
    }
}
