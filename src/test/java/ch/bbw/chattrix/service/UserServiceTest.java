package ch.bbw.chattrix.service;

import ch.bbw.chattrix.exception.EmailAlreadyExistsException;
import ch.bbw.chattrix.exception.UsernameOrPasswordWrongException;
import ch.bbw.chattrix.entity.mariadb.User;
import ch.bbw.chattrix.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void shouldCreateUserHans() {

        when(userRepository.findByeMail("hans@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("1234"))
                .thenReturn("hashed");

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        User result = userService.registerUser(
                "Hans",
                "hans@example.com",
                "1234"
        );

        assertEquals("Hans", result.getDisplayName());
        assertEquals("hans@example.com", result.geteMail());
    }

    @Test
    void shouldThrowIfEmailExists() {

        when(userRepository.findByeMail("hans@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class, () ->
                userService.registerUser("Hans", "hans@example.com", "1234")
        );
    }

    @Test
    void shouldThrowIfPasswordBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("Hans", "hans@example.com", "")
        );
    }

    @Test
    void shouldThrowIfNameBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("", "hans@example.com", "1234")
        );
    }

    @Test
    void shouldThrowIfEmailBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("Hans", "", "1234")
        );
    }

    @Test
    void shouldLoginHans() {

        User user = new User("Hans", "hans@example.com", "hashed");

        when(userRepository.findByeMail("hans@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("1234", "hashed"))
                .thenReturn(true);

        User result = userService.loginUser("hans@example.com", "1234");

        assertEquals("Hans", result.getDisplayName());
    }

    @Test
    void shouldFailLoginWrongPassword() {

        User user = new User("Hans", "hans@example.com", "hashed");

        when(userRepository.findByeMail("hans@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("1234", "hashed"))
                .thenReturn(false);

        assertThrows(UsernameOrPasswordWrongException.class, () ->
                userService.loginUser("hans@example.com", "1234")
        );
    }

    @Test
    void shouldFailLoginUnknownEmail() {

        when(userRepository.findByeMail("hans@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameOrPasswordWrongException.class, () ->
                userService.loginUser("hans@example.com", "1234")
        );
    }

    @Test
    void shouldUpdateUserHans() {
        User existing = new User("Hans", "hans@example.com", "1234");
        existing.setId(1);

        User update = new User("Hans New", "hans@example.com", "newpassword");
        update.setId(1);

        when(userRepository.findByeMail("hans@example.com"))
                .thenReturn(Optional.of(existing));

        when(passwordEncoder.encode("newpassword"))
                .thenReturn("hashed");

        when(userRepository.findById(1))
                .thenReturn(Optional.of(existing));

        // 3. Execute
        User result = userService.updateUser(1, update);

        // (Don't forget to add your assertions here to check if 'result' is correct!)
    }

    @Test
    void shouldFailUpdateUserNotFound() {

        when(userRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                userService.updateUser(1, new User())
        );
    }

    @Test
    void shouldGetHans() {

        User user = new User("Hans", "hans@example.com", "1234");

        when(userRepository.getReferenceById(1))
                .thenReturn(user);

        User result = userService.getUser(1);

        assertEquals("Hans", result.getDisplayName());
    }
}