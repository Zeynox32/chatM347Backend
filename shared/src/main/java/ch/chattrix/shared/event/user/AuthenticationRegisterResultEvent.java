package ch.chattrix.shared.event.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRegisterResultEvent {
    private boolean success;
    private String errorMessage;
}