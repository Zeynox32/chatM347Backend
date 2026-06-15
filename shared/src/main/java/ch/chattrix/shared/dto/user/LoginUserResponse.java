package ch.chattrix.shared.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginUserResponse {
    private String accessToken;
    private String refreshToken;
    private boolean success;
}