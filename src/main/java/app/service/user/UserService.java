package app.service.user;

import app.exception.UserIncorrectPasswordOrUsername;
import app.exception.UserNotFound;
import app.exception.UserWithEmailOrUsernameExists;
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

import static app.exception.ExceptionMessages.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(UserRegisterRequest userRegisterRequest) {

        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(userRegisterRequest.getUsername(), userRegisterRequest.getEmail());

        if (optionalUser.isPresent()) {
            throw new UserWithEmailOrUsernameExists(USER_WITH_EMAIL_OR_USERNAME_EXISTS);
        }

        User user = User.builder()
                .username(userRegisterRequest.getUsername())
                .firstName(userRegisterRequest.getFirstName())
                .lastName(userRegisterRequest.getLastName())
                .email(userRegisterRequest.getEmail())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .role(UserRole.USER)
                .build();

        if (userRepository.findAll().isEmpty()) {
            user.setRole(UserRole.ADMIN);
        }

        userRepository.save(user);
    }

    public User loginUser(UserLoginRequest userLoginRequest) {

        Optional<User> optionalUser = userRepository.findByUsername(userLoginRequest.getUsername());
        if (optionalUser.isEmpty()) {
            throw new UserIncorrectPasswordOrUsername(USER_INCORRECT_PASSWORD_OR_USERNAME);
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
            throw new UserIncorrectPasswordOrUsername(USER_INCORRECT_PASSWORD_OR_USERNAME);
        }

        return user;
    }

    @Transactional
    public void updateUser(UUID id, UserEditRequest userEditRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFound(USER_NOT_FOUND));

        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setEmail(userEditRequest.getEmail());
        user.setPhoneNumber(userEditRequest.getPhoneNumber());

        userRepository.save(user);
    }

    @Transactional
    public void switchRole(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFound(USER_NOT_FOUND));

        user.setRole(user.getRole() == UserRole.USER ? UserRole.ADMIN : UserRole.USER);
        userRepository.save(user);
    }

    public User getById(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUserById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

        userRepository.delete(user);
    }
}
