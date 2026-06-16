package ch.chattrix.authenticationservice.rabbitmq;

import ch.chattrix.authenticationservice.service.AuthenticationService;
import ch.chattrix.shared.command.user.AuthenticationRegisterCommand;
import ch.chattrix.shared.command.user.UserLoginCommand;
import ch.chattrix.shared.event.user.AuthenticationRegisterResultEvent;
import ch.chattrix.shared.event.user.UserLoginResultEvent;
import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import ch.chattrix.shared.response.LoginApiResponse;
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

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        try {
            AuthenticationRegisterCommand command =
                    objectMapper.readValue(
                            message.getBody(),
                            AuthenticationRegisterCommand.class
                    );

            boolean success = authService.register(
                    command.getEmail(),
                    command.getPassword(),
                    command.getUserUuid()
            );

            AuthenticationRegisterResultEvent result =
                    new AuthenticationRegisterResultEvent();

            result.setSuccess(success);

            if (!success) {
                result.setErrorMessage("Registration failed");
            }

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

            AuthenticationRegisterResultEvent errorResult =
                    new AuthenticationRegisterResultEvent();

            errorResult.setSuccess(false);
            errorResult.setErrorMessage(
                    e.getMessage() != null ? e.getMessage() : "Unknown error"
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.AUTH_RESULT_REGISTER,
                    errorResult,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.AUTH_LOGIN_QUEUE)
    public void handleLogin(Message message) {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        try {
            UserLoginCommand command =
                    objectMapper.readValue(
                            message.getBody(),
                            UserLoginCommand.class
                    );

            LoginApiResponse loginUserResponse = authService.login(
                    command.getEmail(),
                    command.getPassword()
            );

            UserLoginResultEvent result =
                    new UserLoginResultEvent();

            result.setSuccess(loginUserResponse.isSuccess());

            if (!loginUserResponse.isSuccess()) {
                result.setErrorMessage("Login failed");
            }

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
            UserLoginResultEvent errorResult =
                    new UserLoginResultEvent();

            errorResult.setSuccess(false);
            errorResult.setErrorMessage(
                    e.getMessage() != null ? e.getMessage() : "Unknown error"
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.AUTH_RESULT_LOGIN,
                    errorResult,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }
}