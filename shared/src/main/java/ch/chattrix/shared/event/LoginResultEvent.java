package ch.chattrix.shared.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResultEvent {
    private boolean success;
    private String errorMessage;
    private String refreshToken;
    private String accessToken;
}
