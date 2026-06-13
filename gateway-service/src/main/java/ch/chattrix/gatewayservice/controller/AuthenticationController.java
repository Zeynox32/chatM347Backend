package ch.chattrix.gatewayservice.controller;

import ch.chattrix.gatewayservice.service.RegistrationService;
import ch.chattrix.shared.dto.user.RegisterUserRequest;
import ch.chattrix.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final RegistrationService authService;

    public AuthenticationController(RegistrationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse register(@RequestBody RegisterUserRequest request) {
        return authService.register(request);
    }
}