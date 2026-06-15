package ch.chattrix.authenticationservice.service;

import ch.chattrix.authenticationservice.entity.RefreshToken;
import ch.chattrix.authenticationservice.entity.UserCredential;
import ch.chattrix.authenticationservice.repository.RefreshTokenRepository;
import ch.chattrix.authenticationservice.repository.UserCredentialRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthenticationService(
            RefreshTokenRepository refreshTokenRepository,
            UserCredentialRepository userCredentialRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userCredentialRepository = userCredentialRepository;
    }

    private String hashPassword(String password) {
        return encoder.encode(password);
    }

    public boolean register(String email, String password, UUID userUuid) {

        UserCredential newUserCredential = new UserCredential();
        newUserCredential.setEmail(email);
        newUserCredential.setUserUuid(userUuid);
        newUserCredential.setPasswordHash(hashPassword(password));
        newUserCredential.setCreatedAt(new Date());
        newUserCredential.setUpdatedAt(new Date());

        userCredentialRepository.save(newUserCredential);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserUuid(userUuid);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedAt(new Date());
        refreshToken.setExpiresAt(
                Date.from(Instant.now().plus(Duration.ofDays(14)))
        );
        refreshTokenRepository.save(refreshToken);

        return true;
    }
}