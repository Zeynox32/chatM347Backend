package ch.chattrix.authenticationservice.rabbitmq;

import ch.chattrix.authenticationservice.service.AuthenticationService;
import ch.chattrix.shared.command.*;
import ch.chattrix.shared.event.BasicRabbitMqResultEvent;
import ch.chattrix.shared.event.GetOneUserEmailDataResultEvent;
import ch.chattrix.shared.event.LoginResultEvent;
import ch.chattrix.shared.event.RefreshTokenResultEvent;
import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.LoginData;
import ch.chattrix.shared.types.RefreshTokenData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationListener {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final AuthenticationService authService;

    public AuthenticationListener(ObjectMapper objectMapper,
                                  RabbitTemplate rabbitTemplate,
                                  AuthenticationService authService) {
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.authService = authService;
    }

    @RabbitListener(queues = Queues.AUTH_REGISTER_QUEUE)
    public void handleRegister(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserCredentialRegisterCommand command =
                    objectMapper.readValue(message.getBody(), UserCredentialRegisterCommand.class);

            ApiResponse<Void> serviceResponse =
                    authService.register(
                            command.getEmail(),
                            command.getPassword(),
                            command.getUserUuid()
                    );

            BasicRabbitMqResultEvent result = new BasicRabbitMqResultEvent();
            result.setSuccess(serviceResponse.isSuccess());
            result.setErrorMessage(
                    serviceResponse.isSuccess()
                            ? null
                            : serviceResponse.getMessage()
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_REGISTER,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            BasicRabbitMqResultEvent result = new BasicRabbitMqResultEvent();
            result.setSuccess(false);
            result.setErrorMessage(
                    e.getMessage() != null
                            ? e.getMessage()
                            : "UNKNOWN_ERROR"
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_REGISTER,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.AUTH_LOGIN_QUEUE)
    public void handleLogin(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserLoginCommand command =
                    objectMapper.readValue(message.getBody(), UserLoginCommand.class);

            ApiResponse<LoginData> serviceResponse =
                    authService.login(command.getEmail(), command.getPassword());

            LoginResultEvent result = new LoginResultEvent();
            result.setSuccess(serviceResponse.isSuccess());
            result.setErrorMessage(
                    serviceResponse.isSuccess()
                            ? null
                            : serviceResponse.getMessage()
            );

            if (serviceResponse.isSuccess() && serviceResponse.getData() != null) {
                result.setAccessToken(serviceResponse.getData().getAccessToken());
                result.setRefreshToken(serviceResponse.getData().getRefreshToken());
            }

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_LOGIN,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            LoginResultEvent result = new LoginResultEvent();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_LOGIN,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.AUTH_REFRESH_QUEUE)
    public void handleRefresh(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserRefreshTokenCommand command =
                    objectMapper.readValue(message.getBody(), UserRefreshTokenCommand.class);

            ApiResponse<RefreshTokenData> serviceResponse =
                    authService.refresh(command.getRefreshToken());

            RefreshTokenResultEvent result = new RefreshTokenResultEvent();
            result.setSuccess(serviceResponse.isSuccess());
            result.setErrorMessage(
                    serviceResponse.isSuccess()
                            ? null
                            : serviceResponse.getMessage()
            );

            if (serviceResponse.isSuccess() && serviceResponse.getData() != null) {
                result.setAccessToken(serviceResponse.getData().getAccessToken());
            }

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_REFRESH,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            RefreshTokenResultEvent result = new RefreshTokenResultEvent();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_REFRESH,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.AUTH_LOGOUT_QUEUE)
    public void handleLogout(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserUuidBasicCommand command =
                    objectMapper.readValue(message.getBody(), UserUuidBasicCommand.class);

            ApiResponse<Void> serviceResponse =
                    authService.logout(command.getUserUuid());

            BasicRabbitMqResultEvent result = new BasicRabbitMqResultEvent();
            result.setSuccess(serviceResponse.isSuccess());
            result.setErrorMessage(
                    serviceResponse.isSuccess()
                            ? null
                            : serviceResponse.getMessage()
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_LOGOUT,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            BasicRabbitMqResultEvent result = new BasicRabbitMqResultEvent();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_LOGOUT,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.AUTH_GET_EMAIL_QUEUE)
    public void handleGetEmail(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserUuidBasicCommand command =
                    objectMapper.readValue(message.getBody(), UserUuidBasicCommand.class);

            ApiResponse<String> serviceResponse =
                    authService.getEmailByUserUuid(command.getUserUuid());

            GetOneUserEmailDataResultEvent result =
                    new GetOneUserEmailDataResultEvent();

            result.setSuccess(serviceResponse.isSuccess());
            result.setErrorMessage(
                    serviceResponse.isSuccess() ? null : serviceResponse.getMessage()
            );

            if (serviceResponse.isSuccess() && serviceResponse.getData() != null) {
                result.setEmail(serviceResponse.getData());
            }

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_GET_EMAIL,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            GetOneUserEmailDataResultEvent result =
                    new GetOneUserEmailDataResultEvent();

            result.setSuccess(false);
            result.setErrorMessage(e.getMessage() != null ? e.getMessage() : "UNKNOWN_ERROR");

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_GET_EMAIL,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.AUTH_EDIT_CREDENTIAL_QUEUE)
    public void handleEditCredential(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserEditCredentialCommand command =
                    objectMapper.readValue(message.getBody(), UserEditCredentialCommand.class);

            ApiResponse<Void> serviceResponse =
                    authService.editCredential(command.getUserUuid(), command.getEmail(), command.getPassword());

            BasicRabbitMqResultEvent result = new BasicRabbitMqResultEvent();
            result.setSuccess(serviceResponse.isSuccess());
            result.setErrorMessage(
                    serviceResponse.isSuccess()
                            ? null
                            : serviceResponse.getMessage()
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_EDIT_CREDENTIAL,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            BasicRabbitMqResultEvent result = new BasicRabbitMqResultEvent();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_EDIT_CREDENTIAL,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.AUTH_DELETE_QUEUE)
    public void handleDelete(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserUuidBasicCommand command =
                    objectMapper.readValue(message.getBody(), UserUuidBasicCommand.class);

            ApiResponse<Void> serviceResponse =
                    authService.delete(
                            command.getUserUuid()
                    );

            BasicRabbitMqResultEvent result = new BasicRabbitMqResultEvent();
            result.setSuccess(serviceResponse.isSuccess());
            result.setErrorMessage(
                    serviceResponse.isSuccess()
                            ? null
                            : serviceResponse.getMessage()
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_DELETE,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            BasicRabbitMqResultEvent result = new BasicRabbitMqResultEvent();
            result.setSuccess(false);
            result.setErrorMessage(
                    e.getMessage() != null
                            ? e.getMessage()
                            : "UNKNOWN_ERROR"
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.AUTHENTICATION_RESPONSE,
                    RoutingKeys.AUTH_RESULT_DELETE,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }
}