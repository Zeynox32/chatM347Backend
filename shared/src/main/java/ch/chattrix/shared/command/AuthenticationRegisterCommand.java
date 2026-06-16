package ch.chattrix.shared.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRegisterCommand {
    private String email;
    private String password;
    private UUID userUuid;
}