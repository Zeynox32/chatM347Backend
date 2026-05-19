package ch.bbw.chattrix.repository;

import ch.bbw.chattrix.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindHansByEmail() {

        User user = new User(
                "Hans",
                "hans@example.com",
                "1234"
        );

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByeMail("hans@example.com");
        assertTrue(result.isPresent());
        assertEquals("Hans", result.get().getUsername());
    }
}