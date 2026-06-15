package ch.chattrix.gatewayservice.rabbitmq;

import ch.chattrix.gatewayservice.aggregator.RegistrationAggregator;
import ch.chattrix.shared.event.user.AuthenticationRegisterResultEvent;
import ch.chattrix.shared.event.user.UserProfileResultEvent;
import ch.chattrix.shared.rabbitmq.Queues;
import org.springframework.amqp.core.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitResultListener {

    private final RegistrationAggregator aggregator;
    private final ObjectMapper objectMapper;

    public RabbitResultListener(RegistrationAggregator aggregator,
                                ObjectMapper objectMapper) {
        this.aggregator = aggregator;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = Queues.AUTH_RESULT_QUEUE)
    public void handleAuth(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();

        AuthenticationRegisterResultEvent event =
                objectMapper.readValue(message.getBody(), AuthenticationRegisterResultEvent.class);

        aggregator.handleAuth(correlationId, event);
    }

    @RabbitListener(queues = Queues.USER_RESULT_QUEUE)
    public void handleUser(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();

        UserProfileResultEvent event =
                objectMapper.readValue(message.getBody(), UserProfileResultEvent.class);

        aggregator.handleUser(correlationId, event);
    }
}