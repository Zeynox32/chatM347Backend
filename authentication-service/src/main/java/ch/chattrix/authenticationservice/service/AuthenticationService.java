package ch.chattrix.authenticationservice.service;

import ch.chattrix.authenticationservice.entity.RefreshToken;
import ch.chattrix.authenticationservice.entity.UserCredential;
import ch.chattrix.authenticationservice.repository.RefreshTokenRepository;
import ch.chattrix.authenticationservice.repository.UserCredentialRepository;
import ch.chattrix.authenticationservice.utils.JwtGenerator;
import ch.chattrix.shared.dto.user.LoginUserResponse;
import ch.chattrix.shared.response.LoginApiResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtGenerator jwtGenerator;

    public AuthenticationService(
            RefreshTokenRepository refreshTokenRepository,
            UserCredentialRepository userCredentialRepository,
            JwtGenerator jwtGenerator
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.jwtGenerator = jwtGenerator;
    }

    private String hashPassword(String password) {
        return encoder.encode(password);
    }

    public boolean register(String email, String password, UUID userUuid) {
        String generatedRefreshToken =
                jwtGenerator.generateRefreshToken(userUuid.toString());

        UserCredential newUserCredential = new UserCredential();
        newUserCredential.setEmail(email);
        newUserCredential.setUserUuid(userUuid);
        newUserCredential.setPasswordHash(hashPassword(password));
        newUserCredential.setCreatedAt(new Date());
        newUserCredential.setUpdatedAt(new Date());

        userCredentialRepository.save(newUserCredential);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserUuid(userUuid);

        refreshToken.setToken(generatedRefreshToken);

        refreshToken.setCreatedAt(new Date());

        refreshToken.setExpiresAt(
                Date.from(Instant.now().plus(Duration.ofDays(14)))
        );

        refreshTokenRepository.save(refreshToken);

        return true;
    }

    @Transactional
    public LoginApiResponse login(String email, String password) {

        boolean success = false;
        UserCredential user = userCredentialRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid credentials")
                );

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String accessToken = jwtGenerator.generateAccessToken(
                user.getUserUuid().toString(),
                user.getEmail()
        );

        String generatedRefreshToken = jwtGenerator.generateRefreshToken(
                user.getUserUuid().toString()
        );

        refreshTokenRepository.deleteByUserUuid(
                user.getUserUuid()
        );

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserUuid(user.getUserUuid());
        refreshToken.setToken(generatedRefreshToken);
        refreshToken.setCreatedAt(new Date());

        refreshToken.setExpiresAt(
                Date.from(Instant.now().plus(Duration.ofDays(14)))
        );

        refreshTokenRepository.save(refreshToken);
        success = true;

        LoginApiResponse loginApiResponse = new LoginApiResponse();
        loginApiResponse.setSuccess(success);
        loginApiResponse.setAccessToken(accessToken);
        loginApiResponse.setRefreshToken(refreshToken.getToken());
        return loginApiResponse;
    }
}