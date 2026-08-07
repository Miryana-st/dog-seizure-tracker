package app.service.user;

import app.exception.NotFoundException;
import app.exception.UserWithEmailOrUsernameExists;
import app.model.dto.user.UserEditRequest;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.repository.user.UserRepository;
import app.security.user.UserData;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static app.exception.ExceptionMessages.*;

@Service
public class UserService implements UserDetailsService {

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

    @Transactional
    public void updateUser(UUID id, UserEditRequest userEditRequest) {
        User user = getById(id);

        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setEmail(userEditRequest.getEmail());
        user.setPhoneNumber(userEditRequest.getPhoneNumber());

        userRepository.save(user);
    }

    @Transactional
    public void switchRole(UUID id) {
        User user = getById(id);

        user.setRole(user.getRole() == UserRole.USER ? UserRole.ADMIN : UserRole.USER);

        userRepository.save(user);
    }

    public User getById(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUserById(UUID id) {

        User user = getById(id);

        userRepository.delete(user);
    }

    public boolean isUserOwned(UUID userId, UUID loggedUserId) {
        return userId.equals(loggedUserId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

        return new UserData(user.getId(), username, user.getPassword(), user.getRole());
    }
}
