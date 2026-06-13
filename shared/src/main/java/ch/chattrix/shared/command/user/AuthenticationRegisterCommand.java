package ch.chattrix.shared.command.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticationRegisterCommand {
    private String email;
    private String password;
    private UUID userUuid;
}