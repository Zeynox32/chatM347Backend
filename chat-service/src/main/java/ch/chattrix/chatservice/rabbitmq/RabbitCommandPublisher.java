package ch.chattrix.chatservice.rabbitmq;

import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import ch.chattrix.shared.rabbitmq.command.UserUsernamesGetCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class RabbitCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendGetUsernamesRequest(
            UserUsernamesGetCommand command,
            String correlationId
    ) {

        rabbitTemplate.convertAndSend(
                Exchanges.USER,
                RoutingKeys.USER_GET_USERNAMES,
                command,
                message -> {
                    message.getMessageProperties()
                            .setCorrelationId(correlationId);
                    return message;
                }
        );
    }
}