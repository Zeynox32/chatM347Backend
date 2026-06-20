package ch.chattrix.authenticationservice.rabbitmq;

import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public DirectExchange authenticationExchange() {
        return new DirectExchange(Exchanges.AUTHENTICATION);
    }

    @Bean
    public DirectExchange authenticationResponseExchange() {
        return new DirectExchange(Exchanges.AUTHENTICATION_RESPONSE);
    }

    @Bean
    public Queue authRegisterQueue() {
        return new Queue(Queues.AUTH_REGISTER_QUEUE, true);
    }

    @Bean
    public Queue authLoginQueue() {
        return new Queue(Queues.AUTH_LOGIN_QUEUE, true);
    }

    @Bean
    public Queue authRefreshTokenQueue() {
        return new Queue(Queues.AUTH_REFRESH_QUEUE, true);
    }

    @Bean
    public Queue authLogoutQueue() {
        return new Queue(Queues.AUTH_LOGOUT_QUEUE, true);
    }

    @Bean
    public Queue authGetEmailQueue() {
        return new Queue(Queues.AUTH_GET_EMAIL_QUEUE, true);
    }

    @Bean
    public Queue authEditCredentialQueue() {
        return new Queue(Queues.AUTH_EDIT_CREDENTIAL_QUEUE, true);
    }

    @Bean
    public Queue authDeleteQueue() {
        return new Queue(Queues.AUTH_DELETE_QUEUE, true);
    }

    @Bean
    public Binding authRegisterBinding() {
        return BindingBuilder
                .bind(authRegisterQueue())
                .to(authenticationExchange())
                .with(RoutingKeys.AUTH_REGISTER);
    }

    @Bean
    public Binding authLoginBinding() {
        return BindingBuilder
                .bind(authLoginQueue())
                .to(authenticationExchange())
                .with(RoutingKeys.AUTH_LOGIN);
    }

    @Bean
    public Binding authRefreshTokenBinding() {
        return BindingBuilder
                .bind(authRefreshTokenQueue())
                .to(authenticationExchange())
                .with(RoutingKeys.AUTH_REFRESH);
    }

    @Bean
    public Binding authLogoutBinding() {
        return BindingBuilder
                .bind(authLogoutQueue())
                .to(authenticationExchange())
                .with(RoutingKeys.AUTH_LOGOUT);
    }

    @Bean
    public Binding authGetEmailBinding() {
        return BindingBuilder
                .bind(authGetEmailQueue())
                .to(authenticationExchange())
                .with(RoutingKeys.AUTH_GET_EMAIL);
    }

    @Bean
    public Binding authEditCredentialBinding() {
        return BindingBuilder
                .bind(authEditCredentialQueue())
                .to(authenticationExchange())
                .with(RoutingKeys.AUTH_EDIT_CREDENTIAL);
    }

    @Bean
    public Binding authDeleteBinding() {
        return BindingBuilder
                .bind(authDeleteQueue())
                .to(authenticationExchange())
                .with(RoutingKeys.AUTH_DELETE);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());

        return template;
    }
}