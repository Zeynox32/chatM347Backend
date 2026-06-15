package ch.chattrix.gatewayservice.controller;

import ch.chattrix.gatewayservice.service.AuthenticationService;
import ch.chattrix.shared.dto.user.LoginUserRequest;
import ch.chattrix.shared.dto.user.RegisterUserRequest;
import ch.chattrix.shared.response.BasicApiResponse;
import ch.chattrix.shared.response.LoginApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public BasicApiResponse register(@RequestBody RegisterUserRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public BasicApiResponse login(@RequestBody LoginUserRequest request, HttpServletResponse response) {
        LoginApiResponse serviceResponse = authenticationService.login(request);
        if (serviceResponse.isSuccess()) {

            ResponseCookie accessCookie = ResponseCookie.from("accessToken", serviceResponse.getAccessToken())
                    .httpOnly(true)
                    .secure(false) //TODO: set on true when the app is on prod.
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(Duration.ofMinutes(60))
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", serviceResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(false) //TODO: set on true when the app is on prod.
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(Duration.ofDays(14))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        }
        BasicApiResponse basicApiResponse = new BasicApiResponse();
        basicApiResponse.setSuccess(serviceResponse.isSuccess());
        basicApiResponse.setMessage(serviceResponse.getMessage());
        return basicApiResponse;
    }
}