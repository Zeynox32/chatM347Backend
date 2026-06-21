package ch.chattrix.userservice.rabbitmq;

import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import ch.chattrix.shared.rabbitmq.command.UserEditUsernameCommand;
import ch.chattrix.shared.rabbitmq.command.UserRegisterCommand;
import ch.chattrix.shared.rabbitmq.command.UserUuidBasicCommand;
import ch.chattrix.shared.rabbitmq.event.BasicRabbitMqResultEvent;
import ch.chattrix.shared.rabbitmq.event.GetAllUsersResultEvent;
import ch.chattrix.shared.rabbitmq.event.GetOneUserBasicDataResultEvent;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.UserAnonymData;
import ch.chattrix.shared.types.UserBaseData;
import ch.chattrix.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

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

    @RabbitListener(queues = Queues.USER_REGISTER_QUEUE)
    public void handleUserCreate(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserRegisterCommand command =
                    objectMapper.readValue(message.getBody(), UserRegisterCommand.class);

            ApiResponse<Void> serviceResponse =
                    userService.create(command.getUsername(), command.getUserUuid());

            BasicRabbitMqResultEvent event = new BasicRabbitMqResultEvent();
            event.setSuccess(serviceResponse.isSuccess());
            event.setErrorMessage(serviceResponse.isSuccess() ? null : serviceResponse.getMessage());

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_REGISTER,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {
            BasicRabbitMqResultEvent event = new BasicRabbitMqResultEvent();
            event.setSuccess(false);
            event.setErrorMessage(e.getMessage() != null ? e.getMessage() : "UNKNOWN_ERROR");

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_REGISTER,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.USER_GET_ALL_QUEUE)
    public void handleGetAllUsers(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            ApiResponse<List<UserAnonymData>> serviceResponse =
                    userService.getAll();

            GetAllUsersResultEvent event = new GetAllUsersResultEvent();
            event.setSuccess(serviceResponse.isSuccess());
            event.setErrorMessage(serviceResponse.isSuccess() ? null : serviceResponse.getMessage());
            event.setUsers(serviceResponse.getData());

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_GET_ALL,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            GetAllUsersResultEvent event = new GetAllUsersResultEvent();
            event.setSuccess(false);
            event.setErrorMessage(e.getMessage() != null ? e.getMessage() : "UNKNOWN_ERROR");

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_GET_ALL,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.USER_GET_BASE_DATA_QUEUE)
    public void handleGetOneUser(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserUuidBasicCommand cmd =
                    objectMapper.readValue(message.getBody(), UserUuidBasicCommand.class);

            UUID userUuid = cmd.getUserUuid();

            ApiResponse<UserBaseData> serviceResponse =
                    userService.getOne(userUuid);

            GetOneUserBasicDataResultEvent event = new GetOneUserBasicDataResultEvent();
            event.setSuccess(serviceResponse.isSuccess());
            event.setErrorMessage(serviceResponse.isSuccess() ? null : serviceResponse.getMessage());

            if (serviceResponse.getData() != null) {
                event.setUserUuid(serviceResponse.getData().getUserUuid());
                event.setUsername(serviceResponse.getData().getUsername());
                event.setCreatedAt(serviceResponse.getData().getCreatedAt());
            }

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_GET_BASE_DATA,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            GetOneUserBasicDataResultEvent event = new GetOneUserBasicDataResultEvent();
            event.setSuccess(false);
            event.setErrorMessage(e.getMessage() != null ? e.getMessage() : "UNKNOWN_ERROR");

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_GET_BASE_DATA,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.USER_EDIT_USERNAME_QUEUE)
    public void handleEditUsername(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserEditUsernameCommand cmd =
                    objectMapper.readValue(message.getBody(), UserEditUsernameCommand.class);

            UUID userUuid = cmd.getUserUuid();

            ApiResponse<Void> serviceResponse =
                    userService.editUsername(userUuid, cmd.getUsername());

            BasicRabbitMqResultEvent event = new BasicRabbitMqResultEvent();
            event.setSuccess(serviceResponse.isSuccess());
            event.setErrorMessage(serviceResponse.isSuccess() ? null : serviceResponse.getMessage());

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_EDIT_USERNAME,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            GetOneUserBasicDataResultEvent event = new GetOneUserBasicDataResultEvent();
            event.setSuccess(false);
            event.setErrorMessage(e.getMessage() != null ? e.getMessage() : "UNKNOWN_ERROR");

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_EDIT_USERNAME,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.USER_DELETE_QUEUE)
    public void handleUserDelete(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserUuidBasicCommand command =
                    objectMapper.readValue(message.getBody(), UserUuidBasicCommand.class);

            ApiResponse<Void> serviceResponse =
                    userService.delete(command.getUserUuid());

            BasicRabbitMqResultEvent event = new BasicRabbitMqResultEvent();
            event.setSuccess(serviceResponse.isSuccess());
            event.setErrorMessage(serviceResponse.isSuccess() ? null : serviceResponse.getMessage());

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_DELETE,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {
            BasicRabbitMqResultEvent event = new BasicRabbitMqResultEvent();
            event.setSuccess(false);
            event.setErrorMessage(e.getMessage() != null ? e.getMessage() : "UNKNOWN_ERROR");

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_DELETE,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }
}