package ch.chattrix.authenticationservice.rabbitmq;

import ch.chattrix.authenticationservice.service.AuthenticationService;
import ch.chattrix.shared.command.AuthenticationRegisterCommand;
import ch.chattrix.shared.command.UserLoginCommand;
import ch.chattrix.shared.command.UserLogoutCommand;
import ch.chattrix.shared.event.BasicRabbitMqResultEvent;
import ch.chattrix.shared.event.LoginResultEvent;
import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.LoginData;
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
            AuthenticationRegisterCommand command =
                    objectMapper.readValue(message.getBody(), AuthenticationRegisterCommand.class);

            ApiResponse<Void> serviceResponse =
                    authService.register(
                            command.getEmail(),
                            command.getPassword(),
                            command.getUserUuid()
                    );

            BasicRabbitMqResultEvent result = new BasicRabbitMqResultEvent();
            result.setSuccess(serviceResponse.isSuccess());
            result.setErrorMessage(serviceResponse.isSuccess() ? null : serviceResponse.getMessage());

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
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
            result.setErrorMessage(e.getMessage() != null ? e.getMessage() : "UNKNOWN_ERROR");

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
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
            result.setAccessToken(serviceResponse.getData().getAccessToken());
            result.setRefreshToken(serviceResponse.getData().getRefreshToken());

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
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
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.AUTH_RESULT_LOGIN,
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

        try {
            UserLogoutCommand command =
                    objectMapper.readValue(message.getBody(), UserLogoutCommand.class);

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
                    Exchanges.USER_RESPONSE,
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
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.AUTH_RESULT_LOGOUT,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }
}