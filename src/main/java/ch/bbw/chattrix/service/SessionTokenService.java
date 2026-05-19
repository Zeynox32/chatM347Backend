package ch.bbw.chattrix.service;

import ch.bbw.chattrix.entity.SessionToken;
import ch.bbw.chattrix.repository.SessionTokenRepository;
import ch.bbw.chattrix.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionTokenService {

    private final SessionTokenRepository sessionTokenRepository;

    public SessionTokenService(SessionTokenRepository sessionTokenRepository) {
        this.sessionTokenRepository = sessionTokenRepository;
    }

    @Transactional
    public SessionToken createForUser(User user) {
        sessionTokenRepository.deleteByUser(user);
        SessionToken sessionToken = new SessionToken(UUID.randomUUID().toString(), user, Instant.now());
        return sessionTokenRepository.save(sessionToken);
    }

    public Optional<SessionToken> findByToken(String token) {
        return sessionTokenRepository.findByToken(token);
    }

    public void deleteByToken(String token) {
        sessionTokenRepository.deleteByToken(token);
    }
}
