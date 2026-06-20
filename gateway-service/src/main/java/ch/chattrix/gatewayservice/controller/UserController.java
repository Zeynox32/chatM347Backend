package ch.chattrix.gatewayservice.controller;

import ch.chattrix.gatewayservice.service.UserService;
import ch.chattrix.shared.dto.EditCredentialRequest;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.UserAnonymData;
import ch.chattrix.shared.types.UserData;
import ch.chattrix.shared.utils.JwtValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtValidator jwtValidator;

    public UserController(UserService userService, JwtValidator jwtValidator) {
        this.userService = userService;
        this.jwtValidator = jwtValidator;
    }

    @GetMapping("/all")
    public ApiResponse<List<UserAnonymData>> getAllUsers(HttpServletRequest request, @CookieValue(value = "accessToken", required = false) String token) {

        if (token == null | !jwtValidator.isTokenValid(token)) {
            return new ApiResponse<>(false, "INVALID_ACCESS_TOKEN", null);
        }

        return userService.getAllUsers();
    }

    @GetMapping("/one")
    public ApiResponse<UserData> getOneUser(
            @CookieValue(value = "accessToken", required = false) String token) {

        if (token == null || !jwtValidator.isTokenValid(token)) {
            return new ApiResponse<>(false, "INVALID_ACCESS_TOKEN", null);
        }

        UUID userUuid = UUID.fromString(jwtValidator.extractSubject(token));

        return userService.getOneUser(userUuid);
    }

    @PatchMapping("/edit/credential")
    public ApiResponse<Void> editCredential(
            @CookieValue(value = "accessToken", required = false) String token,
            @RequestBody EditCredentialRequest request) {

        if (token == null || !jwtValidator.isTokenValid(token)) {
            return new ApiResponse<>(false, "INVALID_ACCESS_TOKEN", null);
        }

        UUID userUuid = UUID.fromString(jwtValidator.extractSubject(token));

        return userService.editCredential(
                request.getEmail(),
                request.getPassword(),
                userUuid
        );
    }
}
