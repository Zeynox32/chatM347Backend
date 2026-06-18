package ch.chattrix.gatewayservice.rabbitmq;

import ch.chattrix.shared.command.*;
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

    private void sendToUserExchange(Object cmd, String routingKey, String correlationId) {
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

    private void sendToAuthenticationExchange(Object cmd, String routingKey, String correlationId) {
        rabbitTemplate.convertAndSend(
                Exchanges.AUTHENTICATION,
                routingKey,
                cmd,
                msg -> {
                    msg.getMessageProperties().setCorrelationId(correlationId);
                    return msg;
                }
        );
    }

    public void sendRegisterRequest(UserCredentialRegisterCommand cmd, String correlationId) {
        sendToAuthenticationExchange(cmd, RoutingKeys.AUTH_REGISTER, correlationId);
    }

    public void sendRegisterUserRequest(UserRegisterCommand cmd, String correlationId) {
        sendToUserExchange(cmd, RoutingKeys.USER_REGISTER, correlationId);
    }

    public void sendLoginRequest(UserLoginCommand cmd, String correlationId) {
        sendToAuthenticationExchange(cmd, RoutingKeys.AUTH_LOGIN, correlationId);
    }

    public void sendRefreshRequest(UserRefreshTokenCommand cmd, String correlationId) {
        sendToAuthenticationExchange(cmd, RoutingKeys.AUTH_REFRESH, correlationId);
    }

    public void sendLogoutRequest(UserLogoutCommand cmd, String correlationId) {
        sendToAuthenticationExchange(cmd, RoutingKeys.AUTH_LOGOUT, correlationId);
    }

}