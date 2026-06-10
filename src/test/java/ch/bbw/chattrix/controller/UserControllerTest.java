package ch.bbw.chattrix.controller;

import ch.bbw.chattrix.dto.authorization.LoginUserRequest;
import ch.bbw.chattrix.dto.authorization.RegisterUserRequest;
import ch.bbw.chattrix.entity.mariadb.SessionToken;
import ch.bbw.chattrix.entity.mariadb.User;
import ch.bbw.chattrix.exception.UsernameOrPasswordWrongException;
import ch.bbw.chattrix.service.SessionTokenService;
import ch.bbw.chattrix.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    SessionTokenService sessionTokenService;

    private SessionToken mockToken;

    @BeforeEach
    void setUp() {
        mockToken = new SessionToken();
        mockToken.setToken("mocked-session-cookie-value");
    }

    @Test
    void shouldRegisterHans() throws Exception {
        User user = new User("Hans", "hans@example.com", "hashed");
        user.setId(1);

        when(userService.registerUser(anyString(), anyString(), anyString()))
                .thenReturn(user);

        when(sessionTokenService.createForUser(any(User.class)))
                .thenReturn(mockToken);

        RegisterUserRequest request =
                new RegisterUserRequest("Hans", "hans@example.com", "1234");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    void shouldLoginHans() throws Exception {
        User user = new User("Hans", "hans@example.com", "hashed");
        user.setId(1);

        when(userService.loginUser(anyString(), anyString()))
                .thenReturn(user);

        when(sessionTokenService.createForUser(any(User.class)))
                .thenReturn(mockToken);

        LoginUserRequest request =
                new LoginUserRequest("hans@example.com", "1234");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailLogin() throws Exception {
        when(userService.loginUser(anyString(), anyString()))
                .thenThrow(new UsernameOrPasswordWrongException());

        LoginUserRequest request =
                new LoginUserRequest("hans@example.com", "wrong");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}