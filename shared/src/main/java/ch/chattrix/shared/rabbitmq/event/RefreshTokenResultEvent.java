package ch.chattrix.shared.rabbitmq.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResultEvent extends BasicRabbitMqResultEvent {
    private String accessToken;
}
