package ch.chattrix.authenticationservice.service;

import ch.chattrix.authenticationservice.entity.RefreshToken;
import ch.chattrix.authenticationservice.entity.UserCredential;
import ch.chattrix.authenticationservice.repository.RefreshTokenRepository;
import ch.chattrix.authenticationservice.repository.UserCredentialRepository;
import ch.chattrix.authenticationservice.utils.JwtGenerator;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.LoginData;
import ch.chattrix.shared.types.RefreshTokenData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
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

    public ApiResponse<Void> register(String email, String password, UUID userUuid) {
        if (userCredentialRepository.findByEmail(email).isPresent()) {
            return new ApiResponse<>(false, "EMAIL_ALREADY_IN_USE", null);
        }
        if (userCredentialRepository.findByUserUuid(userUuid).isPresent()) {
            return new ApiResponse<>(false, "USER_ALREADY_EXISTS", null);
        }

        try {
            String randomRefreshToken = UUID.randomUUID().toString();

            UserCredential credential = new UserCredential();
            credential.setEmail(email);
            credential.setUserUuid(userUuid);
            credential.setPasswordHash(hashPassword(password));
            credential.setCreatedAt(new Date());
            credential.setUpdatedAt(new Date());

            userCredentialRepository.save(credential);

            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUserUuid(userUuid);
            refreshToken.setToken(randomRefreshToken);
            refreshToken.setCreatedAt(new Date());
            refreshToken.setExpiresAt(Date.from(Instant.now().plus(Duration.ofDays(14))));

            refreshTokenRepository.save(refreshToken);

            return new ApiResponse<>(true, "REGISTER_SUCCESS", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, "REGISTER_FAILED", null);
        }
    }

    @Transactional
    public ApiResponse<LoginData> login(String email, String password) {

        UserCredential user = userCredentialRepository.findByEmail(email)
                .orElse(null);

        if (user == null || !encoder.matches(password, user.getPasswordHash())) {
            return new ApiResponse<>(false, "INVALID_CREDENTIALS", null);
        }

        refreshTokenRepository.deleteByUserUuid(user.getUserUuid());

        String accessToken = jwtGenerator.generateAccessToken(
                user.getUserUuid().toString(),
                user.getEmail()
        );

        String refreshToken = UUID.randomUUID().toString();

        RefreshToken entity = new RefreshToken();
        entity.setUserUuid(user.getUserUuid());
        entity.setToken(refreshToken);
        entity.setCreatedAt(new Date());
        entity.setExpiresAt(Date.from(Instant.now().plus(Duration.ofDays(14))));

        refreshTokenRepository.save(entity);

        LoginData data = new LoginData();
        data.setAccessToken(accessToken);
        data.setRefreshToken(refreshToken);

        return new ApiResponse<>(true, "LOGIN_SUCCESS", data);
    }

    @Transactional
    public ApiResponse<RefreshTokenData> refresh(String refreshToken) {

        RefreshToken optionalRefreshToken = refreshTokenRepository.findByToken(refreshToken);

        if (refreshToken == null) {
            return new ApiResponse<>(false, "INVALID_REFRESH_TOKEN", null);
        }

        UUID userUuid = optionalRefreshToken.getUserUuid();
        Optional<UserCredential> user = userCredentialRepository.findByUserUuid(userUuid);

        if (user.isEmpty()) {
            return new ApiResponse<>(false, "USER_NOT_EXISTS", null);
        }

        String accessToken = jwtGenerator.generateAccessToken(
                user.get().getUserUuid().toString(),
                user.get().getEmail()
        );

        RefreshTokenData data = new RefreshTokenData();
        data.setAccessToken(accessToken);

        return new ApiResponse<>(true, "REFRESH_SUCCESS", data);
    }

    @Transactional
    public ApiResponse<Void> logout(UUID userUuid) {

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserUuid(userUuid);

        if (refreshToken.isEmpty()) {
            return new ApiResponse<>(false, "NO_REFRESH_TOKEN_FOUND", null);
        }
        refreshTokenRepository.deleteByUserUuid(userUuid);

        return new ApiResponse<>(true, "LOGOUT_SUCCESS", null);
    }

    public ApiResponse<String> getEmailByUserUuid(UUID userUuid) {

        try {
            Optional<UserCredential> user =
                    userCredentialRepository.findByUserUuid(userUuid);

            return user.map(userCredential -> new ApiResponse<>(
                    true,
                    "EMAIL_LOOKUP_SUCCESS",
                    userCredential.getEmail()
            )).orElseGet(() -> new ApiResponse<>(false, "USER_NOT_FOUND", null));

        } catch (Exception e) {
            return new ApiResponse<>(
                    false,
                    "EMAIL_LOOKUP_FAILED",
                    null
            );
        }
    }

    @Transactional
    public ApiResponse<Void> editCredential(UUID userUuid, String email, String password) {

        UserCredential userCredential = userCredentialRepository.findByUserUuid(userUuid)
                .orElse(null);

        if (userCredential == null) {
            return new ApiResponse<>(false, "NO_CREDENTIAL_FOUND", null);
        }

        if ((email == null || email.isBlank()) &&
                (password == null || password.isBlank())) {
            return new ApiResponse<>(false, "NO_CREDENTIAL_TO_EDIT_FOUND", null);
        }

        if (email != null && !email.isBlank() &&
                userCredentialRepository.findByEmail(email)
                        .filter(u -> !u.getUserUuid().equals(userUuid))
                        .isPresent()) {
            return new ApiResponse<>(false, "EMAIL_ALREADY_EXISTS", null);
        }

        if (email != null && !email.isBlank()) {
            userCredential.setEmail(email);
        }

        if (password != null && !password.isBlank()) {
            userCredential.setPasswordHash(hashPassword(password));
        }

        userCredential.setUpdatedAt(Date.from(Instant.now()));

        return new ApiResponse<>(true, "EDIT_CREDENTIAL_SUCCESS", null);
    }

    public ApiResponse<Void> delete(UUID userUuid) {

        boolean credentialExists = userCredentialRepository.findByUserUuid(userUuid).isPresent();
        boolean refreshTokenExists = refreshTokenRepository.findByUserUuid(userUuid).isPresent();

        if (!credentialExists && !refreshTokenExists) {
            return new ApiResponse<>(false, "NOTHING_TO_DELETE", null);
        }

        try {
            userCredentialRepository.findByUserUuid(userUuid)
                    .ifPresent(userCredentialRepository::delete);

            refreshTokenRepository.findByUserUuid(userUuid)
                    .ifPresent(refreshTokenRepository::delete);

            return new ApiResponse<>(true, "DELETE_SUCCESS", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, "DELETE_FAILED", null);
        }
    }
}