package ch.chattrix.gatewayservice.rabbitmq;

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
    public DirectExchange userResponseExchange() {
        return new DirectExchange(Exchanges.USER_RESPONSE);
    }

    @Bean
    public Queue authResultQueue() {
        return new Queue(Queues.AUTH_REGISTER_RESULT_QUEUE, true);
    }

    @Bean
    public Queue userResultQueue() {
        return new Queue(Queues.USER_CREATE_RESULT_QUEUE, true);
    }

    @Bean
    public Queue userLoginQueue() {
        return new Queue(Queues.AUTH_LOGIN_RESULT_QUEUE, true);
    }

    @Bean
    public Binding authRegisterResultBinding() {
        return BindingBuilder
                .bind(authResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.AUTH_RESULT_REGISTER);
    }

    @Bean
    public Binding userCreateResultBinding() {
        return BindingBuilder
                .bind(userResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.USER_RESULT_CREATE);
    }

    @Bean
    public Binding authloginResultBinding() {
        return BindingBuilder
                .bind(userLoginQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.AUTH_RESULT_LOGIN);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
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