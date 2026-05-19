package ch.bbw.chattrix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionTokenRepository extends JpaRepository<SessionToken, Integer> {
    @EntityGraph(attributePaths = "user")
    Optional<SessionToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUser(User user);
}
