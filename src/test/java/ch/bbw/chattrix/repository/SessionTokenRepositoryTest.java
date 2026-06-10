package ch.bbw.chattrix.repository;

import ch.bbw.chattrix.entity.mariadb.SessionToken;
import ch.bbw.chattrix.entity.mariadb.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SessionTokenRepositoryTest {

    @Autowired
    private SessionTokenRepository sessionTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldReturnEntityWithUser() {
        User user = createUser();
        SessionToken token = new SessionToken("token-123", user, Instant.now());

        entityManager.persist(user);
        entityManager.persist(token);
        entityManager.flush();

        Optional<SessionToken> result = sessionTokenRepository.findByToken("token-123");

        assertTrue(result.isPresent());
        assertEquals("token-123", result.get().getToken());
        assertNotNull(result.get().getUser());
        assertEquals(user.getId(), result.get().getUser().getId());
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        Optional<SessionToken> result = sessionTokenRepository.findByToken("missing");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRemoveEntity() {
        User user = createUser();
        SessionToken token = new SessionToken("to-delete", user, Instant.now());

        entityManager.persist(user);
        entityManager.persist(token);
        entityManager.flush();

        sessionTokenRepository.deleteByToken("to-delete");
        entityManager.flush();

        Optional<SessionToken> result =
                sessionTokenRepository.findByToken("to-delete");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRemoveAllTokensForUser() {
        User user = createUser();

        SessionToken t1 = new SessionToken("t1", user, Instant.now());
        SessionToken t2 = new SessionToken("t2", user, Instant.now());

        entityManager.persist(user);
        entityManager.persist(t1);
        entityManager.persist(t2);
        entityManager.flush();

        sessionTokenRepository.deleteByUser(user);
        entityManager.flush();

        assertTrue(sessionTokenRepository.findByToken("t1").isEmpty());
        assertTrue(sessionTokenRepository.findByToken("t2").isEmpty());
    }

    @Test
    void shouldLoadUserBecauseOfEntityGraph() {
        User user = createUser();
        SessionToken token = new SessionToken("graph-test", user, Instant.now());

        entityManager.persist(user);
        entityManager.persist(token);
        entityManager.flush();
        entityManager.clear();

        SessionToken result = sessionTokenRepository.findByToken("graph-test")
                .orElseThrow();

        assertNotNull(result.getUser().getId());
    }

    private User createUser() {
        User user = new User();
        user.setEMail("hans@example.com");
        user.setPassword("1234");
        user.setDisplayName("Hans");
        return user;
    }
}