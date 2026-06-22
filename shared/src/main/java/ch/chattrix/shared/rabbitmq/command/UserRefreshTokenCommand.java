package ch.chattrix.shared.rabbitmq.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRefreshTokenCommand {
    private String refreshToken;
}
