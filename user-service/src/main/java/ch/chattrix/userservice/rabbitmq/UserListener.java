package ch.chattrix.userservice.rabbitmq;

import ch.chattrix.shared.command.UserProfileCommand;
import ch.chattrix.shared.event.BasicRabbitMqResultEvent;
import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserListener {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final UserService userService;

    public UserListener(ObjectMapper objectMapper,
                        RabbitTemplate rabbitTemplate,
                        UserService userService) {
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.userService = userService;
    }

    @RabbitListener(queues = Queues.USER_CREATE_QUEUE)
    public void handleUserCreate(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserProfileCommand command =
                    objectMapper.readValue(message.getBody(), UserProfileCommand.class);

            ApiResponse<Void> serviceResponse =
                    userService.create(command.getUsername(), command.getUserUuid());

            BasicRabbitMqResultEvent event = new BasicRabbitMqResultEvent();
            event.setSuccess(serviceResponse.isSuccess());
            event.setErrorMessage(
                    serviceResponse.isSuccess()
                            ? null
                            : serviceResponse.getMessage()
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_CREATE,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            BasicRabbitMqResultEvent event = new BasicRabbitMqResultEvent();
            event.setSuccess(false);
            event.setErrorMessage(
                    e.getMessage() != null ? e.getMessage() : "UNKNOWN_ERROR"
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_CREATE,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }
}