package ch.chattrix.userservice.rabbitmq;

import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
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
    public Queue userProfileQueue() {
        return new Queue(Queues.USER_PROFILE_QUEUE, true);
    }

    @Bean
    public Queue userResultQueue() {
        return new Queue(Queues.USER_RESULT_QUEUE, true);
    }

    @Bean
    public Binding userProfileBinding() {
        return BindingBuilder
                .bind(userProfileQueue())
                .to(userExchange())
                .with(RoutingKeys.USER_PROFILE);
    }

    @Bean
    public Binding userResultBinding() {
        return BindingBuilder
                .bind(userResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.USER_RESULT);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());

        return factory;
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
