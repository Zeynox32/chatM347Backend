package ch.chattrix.gatewayservice.rabbitmq;

import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import ch.chattrix.shared.rabbitmq.command.*;
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

    private void sendToAuthExchange(Object cmd, String routingKey, String correlationId) {
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
        sendToAuthExchange(cmd, RoutingKeys.AUTH_REGISTER, correlationId);
    }

    public void sendLoginRequest(UserLoginCommand cmd, String correlationId) {
        sendToAuthExchange(cmd, RoutingKeys.AUTH_LOGIN, correlationId);
    }

    public void sendRefreshRequest(UserRefreshTokenCommand cmd, String correlationId) {
        sendToAuthExchange(cmd, RoutingKeys.AUTH_REFRESH, correlationId);
    }

    public void sendLogoutRequest(UserUuidBasicCommand cmd, String correlationId) {
        sendToAuthExchange(cmd, RoutingKeys.AUTH_LOGOUT, correlationId);
    }

    public void sendRegisterUserRequest(UserRegisterCommand cmd, String correlationId) {
        sendToUserExchange(cmd, RoutingKeys.USER_REGISTER, correlationId);
    }

    public void sendGetAllUsersRequest(EmptyBasicCommand cmd, String correlationId) {
        sendToUserExchange(cmd, RoutingKeys.USER_GET_ALL, correlationId);
    }

    public void sendGetOneUserBaseRequest(UserUuidBasicCommand cmd, String correlationId) {
        sendToUserExchange(cmd, RoutingKeys.USER_GET_BASE_DATA, correlationId);
    }

    public void sendGetOneUserEmailRequest(UserUuidBasicCommand cmd, String correlationId) {
        sendToAuthExchange(cmd, RoutingKeys.AUTH_GET_EMAIL, correlationId);
    }

    public void sendEditCredentialRequest(UserEditCredentialCommand cmd, String correlationId) {
        sendToAuthExchange(cmd, RoutingKeys.AUTH_EDIT_CREDENTIAL, correlationId);
    }

    public void sendEditUsernameRequest(UserEditUsernameCommand cmd, String correlationId) {
        sendToUserExchange(cmd, RoutingKeys.USER_EDIT_USERNAME, correlationId);
    }

    public void sendUserDeletionRequest(UserUuidBasicCommand cmd, String correlationId) {
        sendToUserExchange(cmd, RoutingKeys.USER_DELETE, correlationId);
    }

    public void sendAuthDeletionRequest(UserUuidBasicCommand cmd, String correlationId) {
        sendToAuthExchange(cmd, RoutingKeys.AUTH_DELETE, correlationId);
    }
}