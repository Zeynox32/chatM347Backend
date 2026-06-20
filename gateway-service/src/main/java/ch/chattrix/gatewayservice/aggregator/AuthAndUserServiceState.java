package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.event.BasicRabbitMqResultEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthAndUserServiceState {
    private BasicRabbitMqResultEvent auth;
    private BasicRabbitMqResultEvent user;
}