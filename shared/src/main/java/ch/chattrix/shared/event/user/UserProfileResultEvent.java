package ch.chattrix.shared.event.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResultEvent {
    private boolean success;
    private String errorMessage;
}
