package ch.chattrix.gatewayservice.rabbitmq;

import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public DirectExchange userResponseExchange() {
        return new DirectExchange(Exchanges.USER_RESPONSE);
    }

    @Bean
    public DirectExchange authenticationResponseExchange() {
        return new DirectExchange(Exchanges.AUTHENTICATION_RESPONSE);
    }

    @Bean
    public Queue userRegisterResultQueue() {
        return new Queue(Queues.USER_REGISTER_RESULT_QUEUE, true);
    }

    @Bean
    public Queue userGetAllResultQueue() {
        return new Queue(Queues.USER_GET_ALL_RESULT_QUEUE, true);
    }

    @Bean
    public Queue userGetBaseDataResultQueue() {
        return new Queue(Queues.USER_GET_BASE_DATA_RESULT_QUEUE, true);
    }

    @Bean
    public Queue authRegisterResultQueue() {
        return new Queue(Queues.AUTH_REGISTER_RESULT_QUEUE, true);
    }

    @Bean
    public Queue authDeleteResultQueue() {
        return new Queue(Queues.AUTH_DELETE_RESULT_QUEUE, true);
    }

    @Bean
    public Queue userDeleteResultQueue() {
        return new Queue(Queues.USER_DELETE_RESULT_QUEUE, true);
    }

    @Bean
    public Queue authLoginResultQueue() {
        return new Queue(Queues.AUTH_LOGIN_RESULT_QUEUE, true);
    }

    @Bean
    public Queue authRefreshResultQueue() {
        return new Queue(Queues.AUTH_REFRESH_RESULT_QUEUE, true);
    }

    @Bean
    public Queue authLogoutResultQueue() {
        return new Queue(Queues.AUTH_LOGOUT_RESULT_QUEUE, true);
    }

    @Bean
    public Queue authGetEmailResultQueue() {
        return new Queue(Queues.AUTH_GET_EMAIL_RESULT_QUEUE, true);
    }

    @Bean
    public Queue authEditCredentialResultQueue() {
        return new Queue(Queues.AUTH_EDIT_CREDENTIAL_RESULT_QUEUE, true);
    }

    @Bean
    public Queue userEditUsernameResultQueue() {
        return new Queue(Queues.USER_EDIT_USERNAME_RESULT_QUEUE, true);
    }

    @Bean
    public Binding userRegisterResultBinding() {
        return BindingBuilder
                .bind(userRegisterResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.USER_RESULT_REGISTER);
    }

    @Bean
    public Binding userGetAllResultBinding() {
        return BindingBuilder
                .bind(userGetAllResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.USER_RESULT_GET_ALL);
    }

    @Bean
    public Binding userGetBaseDataResultBinding() {
        return BindingBuilder
                .bind(userGetBaseDataResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.USER_RESULT_GET_BASE_DATA);
    }

    @Bean
    public Binding authRegisterResultBinding() {
        return BindingBuilder
                .bind(authRegisterResultQueue())
                .to(authenticationResponseExchange())
                .with(RoutingKeys.AUTH_RESULT_REGISTER);
    }

    @Bean
    public Binding authLoginResultBinding() {
        return BindingBuilder
                .bind(authLoginResultQueue())
                .to(authenticationResponseExchange())
                .with(RoutingKeys.AUTH_RESULT_LOGIN);
    }

    @Bean
    public Binding authRefreshResultBinding() {
        return BindingBuilder
                .bind(authRefreshResultQueue())
                .to(authenticationResponseExchange())
                .with(RoutingKeys.AUTH_RESULT_REFRESH);
    }

    @Bean
    public Binding authLogoutResultBinding() {
        return BindingBuilder
                .bind(authLogoutResultQueue())
                .to(authenticationResponseExchange())
                .with(RoutingKeys.AUTH_RESULT_LOGOUT);
    }

    @Bean
    public Binding authGetEmailResultBinding() {
        return BindingBuilder
                .bind(authGetEmailResultQueue())
                .to(authenticationResponseExchange())
                .with(RoutingKeys.AUTH_RESULT_GET_EMAIL);
    }

    @Bean
    public Binding authEditCredentialResultBinding() {
        return BindingBuilder
                .bind(authEditCredentialResultQueue())
                .to(authenticationResponseExchange())
                .with(RoutingKeys.AUTH_RESULT_EDIT_CREDENTIAL);
    }

    @Bean
    public Binding userEditUsernameResultBinding() {
        return BindingBuilder
                .bind(userEditUsernameResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.USER_RESULT_EDIT_USERNAME);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public Binding userDeleteResultBinding() {
        return BindingBuilder
                .bind(userDeleteResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.USER_RESULT_DELETE);
    }

    @Bean
    public Binding authDeleteResultBinding() {
        return BindingBuilder
                .bind(authDeleteResultQueue())
                .to(authenticationResponseExchange())
                .with(RoutingKeys.AUTH_RESULT_DELETE);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        template.setMessageConverter(messageConverter());
        template.setMandatory(true);

        template.setReturnsCallback(returned -> {
            System.err.println("UNROUTED MESSAGE");
            System.err.println("Exchange: " + returned.getExchange());
            System.err.println("RoutingKey: " + returned.getRoutingKey());
            System.err.println("Reply: " + returned.getReplyText());
        });

        return template;
    }
}