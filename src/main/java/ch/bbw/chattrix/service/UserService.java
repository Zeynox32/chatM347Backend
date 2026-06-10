package ch.bbw.chattrix.service;

import ch.bbw.chattrix.exception.EmailAlreadyExistsException;
import ch.bbw.chattrix.exception.UsernameOrPasswordWrongException;
import ch.bbw.chattrix.entity.mariadb.User;
import ch.bbw.chattrix.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String name, String eMail, String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        } else if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        } else if (eMail == null || eMail.isBlank()) {
            throw new IllegalArgumentException("E-Mail cannot be empty");
        } else if (userRepository.findByeMail(eMail).isPresent()) {
            throw new EmailAlreadyExistsException();
        }else {
            String hashedPassword = passwordEncoder.encode(password);
            User user = new User(name, eMail, hashedPassword);
            return userRepository.save(user);
        }
    }

    public User loginUser(String eMail, String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }else if (eMail == null || eMail.isBlank()) {
            throw new IllegalArgumentException("E-Mail cannot be empty");
        } else {
            User user = userRepository.findByeMail(eMail)
                    .orElseThrow(UsernameOrPasswordWrongException::new);

            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            } else {
                throw new UsernameOrPasswordWrongException();
            }
        }
    }

    public User updateUser(Integer authenticatedUserId, User updatedUserData) {
        Optional<User> oldUser = userRepository.findById(authenticatedUserId);
        if (oldUser.isEmpty()) {
            throw new RuntimeException("User not found: " + authenticatedUserId);
        }

        User existingUser = oldUser.get();

        if (updatedUserData.getEMail() != null && !updatedUserData.getEMail().isBlank()) {

            Optional<User> userWithSameEmail = userRepository.findByeMail(updatedUserData.getEMail());
            if (userWithSameEmail.isPresent()
                    && !userWithSameEmail.get().getId().equals(existingUser.getId())) {
                throw new EmailAlreadyExistsException();
            }

            existingUser.setEMail(updatedUserData.getEMail());
        }

        if (updatedUserData.getDisplayName() != null && !updatedUserData.getDisplayName().isBlank()) {
            existingUser.setDisplayName(updatedUserData.getDisplayName());
        }

        if (updatedUserData.getPassword() != null && !updatedUserData.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUserData.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Integer authenticatedUserId) {

        User existingUser = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new RuntimeException("User not found: " + authenticatedUserId));

        userRepository.delete(existingUser);
    }

    public User getUser(Integer authenticatedUserId) {
        return userRepository.getReferenceById(authenticatedUserId);
    }
}
