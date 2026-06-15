package ch.chattrix.userservice.rabbitmq;

import ch.chattrix.shared.command.user.UserProfileCommand;
import ch.chattrix.shared.event.user.UserProfileResultEvent;
import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
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
    public void handleRegister(Message message) {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        try {
            UserProfileCommand command =
                    objectMapper.readValue(
                            message.getBody(),
                            UserProfileCommand.class
                    );

            boolean success = userService.create(
                    command.getUsername(),
                    command.getUserUuid()
            );

            UserProfileResultEvent result =
                    new UserProfileResultEvent();

            result.setSuccess(success);

            if (!success) {
                result.setErrorMessage("User creation failed");
            }

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_CREATE,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            UserProfileResultEvent result =
                    new UserProfileResultEvent();

            result.setSuccess(false);
            result.setErrorMessage(
                    e.getMessage() != null ? e.getMessage() : "Unknown error"
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_CREATE,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }
}