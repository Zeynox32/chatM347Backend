package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.event.user.AuthenticationRegisterResultEvent;
import ch.chattrix.shared.event.user.UserProfileResultEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationResult {
    private AuthenticationRegisterResultEvent authentication;
    private UserProfileResultEvent user;
}
