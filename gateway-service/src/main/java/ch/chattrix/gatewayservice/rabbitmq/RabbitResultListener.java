package ch.chattrix.gatewayservice.rabbitmq;

import ch.chattrix.gatewayservice.aggregator.*;
import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.event.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class RabbitResultListener {

    private final RegistrationAggregator registrationAggregator;
    private final LoginAggregator loginAggregator;
    private final ObjectMapper objectMapper;
    private final LogoutAggregator logoutAggregator;
    private final RefreshTokenAggregator refreshTokenAggregator;
    private final GetAllUsersAggregator getAllUsersAggregator;
    private final GetOneUserAggregator getOneUserAggregator;
    private final EditCredentialAggregator editCredentialAggregator;
    private final EditUsernameAggregator editUsernameAggregator;
    private final DeleteUserAggregator deleteUserAggregator;

    public RabbitResultListener(
            RegistrationAggregator registrationAggregator,
            LoginAggregator loginAggregator,
            ObjectMapper objectMapper,
            RefreshTokenAggregator refreshTokenAggregator,
            LogoutAggregator logoutAggregator,
            GetOneUserAggregator getOneUserAggregator,
            GetAllUsersAggregator getAllUsersAggregator,
            EditCredentialAggregator editCredentialAggregator, EditUsernameAggregator editUsernameAggregator, DeleteUserAggregator deleteUserAggregator) {
        this.registrationAggregator = registrationAggregator;
        this.loginAggregator = loginAggregator;
        this.objectMapper = objectMapper;
        this.logoutAggregator = logoutAggregator;
        this.refreshTokenAggregator = refreshTokenAggregator;
        this.getAllUsersAggregator = getAllUsersAggregator;
        this.getOneUserAggregator = getOneUserAggregator;
        this.editCredentialAggregator = editCredentialAggregator;
        this.editUsernameAggregator = editUsernameAggregator;
        this.deleteUserAggregator = deleteUserAggregator;
    }

    @RabbitListener(queues = Queues.AUTH_REGISTER_RESULT_QUEUE)
    public void handleAuthRegister(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        BasicRabbitMqResultEvent event =
                objectMapper.readValue(message.getBody(), BasicRabbitMqResultEvent.class);

        registrationAggregator.handleAuth(correlationId, event);
    }

    @RabbitListener(queues = Queues.USER_REGISTER_RESULT_QUEUE)
    public void handleUserCreate(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        BasicRabbitMqResultEvent event =
                objectMapper.readValue(message.getBody(), BasicRabbitMqResultEvent.class);

        registrationAggregator.handleUser(correlationId, event);
    }

    @RabbitListener(queues = Queues.AUTH_LOGIN_RESULT_QUEUE)
    public void handleAuthLogin(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        LoginResultEvent event =
                objectMapper.readValue(message.getBody(), LoginResultEvent.class);

        loginAggregator.completeLogin(correlationId, event);
    }

    @RabbitListener(queues = Queues.AUTH_REFRESH_RESULT_QUEUE)
    public void handleRefreshToken(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        RefreshTokenResultEvent event =
                objectMapper.readValue(message.getBody(), RefreshTokenResultEvent.class);

        refreshTokenAggregator.completeRefreshToken(correlationId, event);
    }

    @RabbitListener(queues = Queues.AUTH_LOGOUT_RESULT_QUEUE)
    public void handleAuthLogout(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        BasicRabbitMqResultEvent event =
                objectMapper.readValue(message.getBody(), BasicRabbitMqResultEvent.class);

        logoutAggregator.completeLogout(correlationId, event);
    }

    @RabbitListener(queues = Queues.USER_GET_ALL_RESULT_QUEUE)
    public void handleGetAllUsers(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        GetAllUsersResultEvent event =
                objectMapper.readValue(message.getBody(), GetAllUsersResultEvent.class);

        getAllUsersAggregator.completeGetAllUsers(correlationId, event);
    }

    @RabbitListener(queues = Queues.USER_GET_BASE_DATA_RESULT_QUEUE)
    public void handleBase(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        GetOneUserBasicDataResultEvent event =
                objectMapper.readValue(message.getBody(),
                        GetOneUserBasicDataResultEvent.class);

        getOneUserAggregator.handleUserBaseData(correlationId, event);
    }


    @RabbitListener(queues = Queues.AUTH_GET_EMAIL_RESULT_QUEUE)
    public void handleEmail(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        GetOneUserEmailDataResultEvent event =
                objectMapper.readValue(message.getBody(),
                        GetOneUserEmailDataResultEvent.class);

        getOneUserAggregator.handleEmail(correlationId, event);
    }

    @RabbitListener(queues = Queues.AUTH_EDIT_CREDENTIAL_RESULT_QUEUE)
    public void handleEditCredential(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        BasicRabbitMqResultEvent event =
                objectMapper.readValue(message.getBody(),
                        BasicRabbitMqResultEvent.class);

        editCredentialAggregator.completeEditCredential(correlationId, event);
    }

    @RabbitListener(queues = Queues.USER_EDIT_USERNAME_RESULT_QUEUE)
    public void handleEditUsername(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        BasicRabbitMqResultEvent event =
                objectMapper.readValue(message.getBody(),
                        BasicRabbitMqResultEvent.class);

        editUsernameAggregator.completeEditUser(correlationId, event);
    }

    @RabbitListener(queues = Queues.AUTH_DELETE_RESULT_QUEUE)
    public void handleAuthDelete(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        BasicRabbitMqResultEvent event =
                objectMapper.readValue(message.getBody(), BasicRabbitMqResultEvent.class);

        deleteUserAggregator.handleAuthenticationDelete(correlationId, event);
    }

    @RabbitListener(queues = Queues.USER_DELETE_RESULT_QUEUE)
    public void handleUserDelete(Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        BasicRabbitMqResultEvent event =
                objectMapper.readValue(message.getBody(), BasicRabbitMqResultEvent.class);

        deleteUserAggregator.handleUserDelete(correlationId, event);
    }
}