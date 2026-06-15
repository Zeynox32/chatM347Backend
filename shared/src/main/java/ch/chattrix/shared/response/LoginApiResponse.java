package ch.chattrix.shared.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginApiResponse {
    private boolean success;
    private String accessToken;
    private String refreshToken;
    private String message;
}
