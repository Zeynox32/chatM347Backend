package ch.chattrix.gatewayservice.controller;

import ch.chattrix.gatewayservice.service.AuthenticationService;
import ch.chattrix.gatewayservice.service.UserService;
import ch.chattrix.shared.dto.EditCredentialRequest;
import ch.chattrix.shared.dto.EditUsernameRequest;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.UserAnonymData;
import ch.chattrix.shared.types.UserData;
import ch.chattrix.shared.utils.JwtValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtValidator jwtValidator;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, JwtValidator jwtValidator, AuthenticationService authenticationService) {
        this.userService = userService;
        this.jwtValidator = jwtValidator;
        this.authenticationService = authenticationService;
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

        return authenticationService.editCredential(
                request.getEmail(),
                request.getPassword(),
                userUuid
        );
    }

    @PatchMapping("/edit/username")
    public ApiResponse<Void> editUsername(
            @CookieValue(value = "accessToken", required = false) String token,
            @RequestBody EditUsernameRequest request) {

        if (token == null || !jwtValidator.isTokenValid(token)) {
            return new ApiResponse<>(false, "INVALID_ACCESS_TOKEN", null);
        }

        UUID userUuid = UUID.fromString(jwtValidator.extractSubject(token));

        return userService.editUsername(
                request.getUsername(),
                userUuid
        );
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteUser(
            @CookieValue(value = "accessToken", required = false) String token, HttpServletResponse response) {

        if (token == null || !jwtValidator.isTokenValid(token)) {
            return new ApiResponse<>(false, "INVALID_ACCESS_TOKEN", null);
        }

        UUID userUuid = UUID.fromString(jwtValidator.extractSubject(token));

        deleteCookies(response);


        return userService.deleteUser(
                userUuid
        );
    }

    static void deleteCookies(HttpServletResponse response) {
        ResponseCookie deleteAccess = ResponseCookie.from("accessToken", "").httpOnly(true).secure(false).path("/").sameSite("Lax").maxAge(0).build();

        ResponseCookie deleteRefresh = ResponseCookie.from("refreshToken", "").httpOnly(true).secure(false).path("/").sameSite("Lax").maxAge(0).build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefresh.toString());
    }
}
