package ch.bbw.chattrix.controller;

import ch.bbw.chattrix.dto.authorization.LoginUserRequest;
import ch.bbw.chattrix.dto.authorization.LoginUserResponse;
import ch.bbw.chattrix.dto.authorization.RegisterUserRequest;
import ch.bbw.chattrix.dto.authorization.RegisterUserResponse;
import ch.bbw.chattrix.entity.mariadb.User;
import ch.bbw.chattrix.exception.EmailAlreadyExistsException;
import ch.bbw.chattrix.exception.UsernameOrPasswordWrongException;
import ch.bbw.chattrix.security.AuthenticatedUser;
import ch.bbw.chattrix.security.SessionAuthenticationFilter;
import ch.bbw.chattrix.service.SessionTokenService;
import ch.bbw.chattrix.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final SessionTokenService sessionTokenService;

    public UserController(
            UserService userService,
            SessionTokenService sessionTokenService
    ) {
        this.userService = userService;
        this.sessionTokenService = sessionTokenService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody RegisterUserRequest request) {
        try {
            User user = userService.addUser(
                    request.displayName(),
                    request.eMail(),
                    request.password()
            );

            String sessionToken = sessionTokenService
                    .createForUser(user)
                    .getToken();

            return ResponseEntity
                    .created(URI.create("/user/" + user.getId()))
                    .header(HttpHeaders.SET_COOKIE, createSessionCookie(sessionToken).toString())
                    .body(new RegisterUserResponse(user.getId()));

        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "E-Mail already exists"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginUserRequest request) {
        try {
            User user = userService.loginUser(
                    request.eMail(),
                    request.password()
            );

            String sessionToken = sessionTokenService
                    .createForUser(user)
                    .getToken();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, createSessionCookie(sessionToken).toString())
                    .body(new LoginUserResponse(user.getId(), user.getUsername()));

        } catch (UsernameOrPasswordWrongException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Username or Password is wrong"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(HttpServletRequest request) {
        readSessionToken(request).ifPresent(sessionTokenService::deleteByToken);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearSessionCookie().toString())
                .build();
    }

    @PutMapping
    public ResponseEntity<?> updateUser(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody User user
    ) {
        try {
            User updatedUser = userService.updateUser(authenticatedUser.id(), user);
            return ResponseEntity.ok(updatedUser);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUser(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        try {
            User user = userService.getUser(authenticatedUser.id());
            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private ResponseCookie createSessionCookie(String sessionToken) {
        return ResponseCookie.from(SessionAuthenticationFilter.SESSION_COOKIE_NAME, sessionToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .build();
    }

    private ResponseCookie clearSessionCookie() {
        return ResponseCookie.from(SessionAuthenticationFilter.SESSION_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
    }

    private Optional<String> readSessionToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie ->
                        SessionAuthenticationFilter.SESSION_COOKIE_NAME
                                .equals(cookie.getName())
                )
                .map(Cookie::getValue)
                .findFirst();
    }
}