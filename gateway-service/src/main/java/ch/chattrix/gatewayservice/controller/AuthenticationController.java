package ch.chattrix.gatewayservice.controller;

import ch.chattrix.gatewayservice.service.AuthenticationService;
import ch.chattrix.shared.dto.LoginUserRequest;
import ch.chattrix.shared.dto.RegisterUserRequest;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.LoginData;
import ch.chattrix.shared.utils.JwtValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtValidator jwtValidator;

    public AuthenticationController(AuthenticationService authenticationService, JwtValidator jwtValidator) {
        this.authenticationService = authenticationService;
        this.jwtValidator = jwtValidator;

    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterUserRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestBody LoginUserRequest request, HttpServletResponse response) {

        ApiResponse<LoginData> serviceResponse = authenticationService.login(request);

        if (!serviceResponse.isSuccess()) {
            return new ApiResponse<>(false, serviceResponse.getMessage(), null);
        }

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", serviceResponse.getData().getAccessToken()).httpOnly(true).secure(false).path("/").sameSite("Lax").maxAge(Duration.ofMinutes(60)).build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", serviceResponse.getData().getRefreshToken()).httpOnly(true).secure(false).path("/").sameSite("Lax").maxAge(Duration.ofDays(14)).build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return new ApiResponse<>(true, serviceResponse.getMessage(), null);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {

        String token = null;

        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null || !jwtValidator.isTokenValid(token)) {
            return new ApiResponse<>(false, "INVALID_TOKEN", null);
        }

        UUID userUuid = UUID.fromString(jwtValidator.extractSubject(token));

        ApiResponse<Void> serviceResponse = authenticationService.logout(userUuid);

        if (!serviceResponse.isSuccess()) {
            return serviceResponse;
        }

        ResponseCookie deleteAccess = ResponseCookie.from("accessToken", "").httpOnly(true).secure(false).path("/").sameSite("Lax").maxAge(0).build();

        ResponseCookie deleteRefresh = ResponseCookie.from("refreshToken", "").httpOnly(true).secure(false).path("/").sameSite("Lax").maxAge(0).build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefresh.toString());

        return serviceResponse;
    }
}