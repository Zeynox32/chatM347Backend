package ch.chattrix.gatewayservice.rabbitmq;

import ch.chattrix.shared.command.UserCredentialRegisterCommand;
import ch.chattrix.shared.command.UserLoginCommand;
import ch.chattrix.shared.command.UserLogoutCommand;
import ch.chattrix.shared.command.UserRegisterCommand;
import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitCommandPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    private void send(Object cmd, String routingKey, String correlationId) {
        rabbitTemplate.convertAndSend(
                Exchanges.USER,
                routingKey,
                cmd,
                msg -> {
                    msg.getMessageProperties().setCorrelationId(correlationId);
                    return msg;
                }
        );
    }

    public void sendRegisterRequest(UserCredentialRegisterCommand cmd, String correlationId) {
        send(cmd, RoutingKeys.AUTH_REGISTER, correlationId);
    }

    public void sendCreateUserRequest(UserRegisterCommand cmd, String correlationId) {
        send(cmd, RoutingKeys.USER_REGISTER, correlationId);
    }

    public void sendLoginRequest(UserLoginCommand cmd, String correlationId) {
        send(cmd, RoutingKeys.AUTH_LOGIN, correlationId);
    }

    public void sendLogoutRequest(UserLogoutCommand cmd, String correlationId) {
        send(cmd, RoutingKeys.AUTH_LOGOUT, correlationId);
    }

}