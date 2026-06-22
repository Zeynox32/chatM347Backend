package ch.chattrix.chatservice.rabbitmq;

import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.event.GetUsernamesResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class RabbitResultListener {

    private final GetUsernamesAggregator aggregator;

    @RabbitListener(queues = Queues.USER_GET_USERNAMES_RESULT_QUEUE)
    public void handleGetUsernamesResult(
            GetUsernamesResultEvent event,
            Message message
    ) {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        if (correlationId == null) {
            return;
        }

        aggregator.complete(correlationId, event);
    }
}