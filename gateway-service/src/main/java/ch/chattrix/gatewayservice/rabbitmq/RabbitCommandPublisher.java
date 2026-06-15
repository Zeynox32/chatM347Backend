package ch.chattrix.gatewayservice.rabbitmq;

import ch.chattrix.shared.command.user.AuthenticationRegisterCommand;
import ch.chattrix.shared.command.user.UserLoginCommand;
import ch.chattrix.shared.command.user.UserProfileCommand;
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

    public void sendRegisterRequest(AuthenticationRegisterCommand cmd, String correlationId) {
        rabbitTemplate.convertAndSend(
                Exchanges.USER,
                RoutingKeys.AUTH_REGISTER,
                cmd,
                msg -> {
                    msg.getMessageProperties().setCorrelationId(correlationId);
                    return msg;
                }
        );
    }

    public void sendCreateUserRequest(UserProfileCommand cmd, String correlationId) {
        rabbitTemplate.convertAndSend(
                Exchanges.USER,
                RoutingKeys.USER_CREATE,
                cmd,
                msg -> {
                    msg.getMessageProperties().setCorrelationId(correlationId);
                    return msg;
                }
        );
    }

    public void sendLoginRequest(UserLoginCommand cmd, String correlationId) {
        rabbitTemplate.convertAndSend(
                Exchanges.USER,
                RoutingKeys.AUTH_LOGIN,
                cmd,
                msg -> {
                    msg.getMessageProperties().setCorrelationId(correlationId);
                    return msg;
                }
        );
    }
}
