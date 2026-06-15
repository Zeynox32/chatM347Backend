package ch.chattrix.gatewayservice.rabbitmq;

import ch.chattrix.gatewayservice.aggregator.LoginAggregator;
import ch.chattrix.gatewayservice.aggregator.RegistrationAggregator;
import ch.chattrix.shared.event.user.AuthenticationRegisterResultEvent;
import ch.chattrix.shared.event.user.UserLoginResultEvent;
import ch.chattrix.shared.event.user.UserProfileResultEvent;
import ch.chattrix.shared.rabbitmq.Queues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitResultListener {

    private final RegistrationAggregator registrationAggregator;
    private final LoginAggregator loginAggregator;
    private final ObjectMapper objectMapper;

    public RabbitResultListener(
            RegistrationAggregator registrationAggregator,
            ObjectMapper objectMapper,
            LoginAggregator loginAggregator
    ) {
        this.registrationAggregator = registrationAggregator;
        this.objectMapper = objectMapper;
        this.loginAggregator = loginAggregator;
    }

    @RabbitListener(queues = Queues.AUTH_REGISTER_RESULT_QUEUE)
    public void handleAuth(Message message) throws Exception {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        AuthenticationRegisterResultEvent event =
                objectMapper.readValue(
                        message.getBody(),
                        AuthenticationRegisterResultEvent.class
                );

        registrationAggregator.handleAuth(correlationId, event);
    }

    @RabbitListener(queues = Queues.USER_CREATE_RESULT_QUEUE)
    public void handleUser(Message message) throws Exception {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        UserProfileResultEvent event =
                objectMapper.readValue(
                        message.getBody(),
                        UserProfileResultEvent.class
                );

        registrationAggregator.handleUser(correlationId, event);
    }

    @RabbitListener(queues = Queues.AUTH_LOGIN_RESULT_QUEUE)
    public void handleLogin(Message message) throws Exception {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        UserLoginResultEvent event =
                objectMapper.readValue(
                        message.getBody(),
                        UserLoginResultEvent.class
                );

        loginAggregator.handleLogin(correlationId, event);
    }
}