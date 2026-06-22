package ch.chattrix.shared.rabbitmq.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEditCredentialCommand {
    private UUID userUuid;
    private String email;
    private String password;
}
