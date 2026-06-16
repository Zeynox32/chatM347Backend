package ch.chattrix.authenticationservice.rabbitmq;

import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(Exchanges.USER);
    }

    @Bean
    public DirectExchange userResponseExchange() {
        return new DirectExchange(Exchanges.USER_RESPONSE);
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
    public Queue authLogoutQueue() {
        return new Queue(Queues.AUTH_LOGOUT_QUEUE, true);
    }

    @Bean
    public Binding authRegisterBinding() {
        return BindingBuilder
                .bind(authRegisterQueue())
                .to(userExchange())
                .with(RoutingKeys.AUTH_REGISTER);
    }

    @Bean
    public Binding authLoginBinding() {
        return BindingBuilder
                .bind(authLoginQueue())
                .to(userExchange())
                .with(RoutingKeys.AUTH_LOGIN);
    }

    @Bean
    public Binding authLogoutBinding() {
        return BindingBuilder
                .bind(authLogoutQueue())
                .to(userExchange())
                .with(RoutingKeys.AUTH_LOGOUT);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());

        return template;
    }
}