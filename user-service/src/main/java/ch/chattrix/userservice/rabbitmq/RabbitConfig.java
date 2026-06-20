package ch.chattrix.userservice.rabbitmq;

import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
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
    public Queue userRegisterQueue() {
        return new Queue(Queues.USER_REGISTER_QUEUE, true);
    }

    @Bean
    public Queue getAllUsersQueue() {
        return new Queue(Queues.USER_GET_ALL_QUEUE, true);
    }

    @Bean
    public Queue getOneUserBaseQueue() {
        return new Queue(Queues.USER_GET_BASE_DATA_QUEUE, true);
    }

    @Bean
    public Queue editUserNameQueue() {
        return new Queue(Queues.USER_EDIT_USERNAME_QUEUE, true);
    }

    @Bean
    public Queue userDeleteQueue() {
        return new Queue(Queues.USER_DELETE_QUEUE, true);
    }

    @Bean
    public Queue userRegisterResultQueue() {
        return new Queue(Queues.USER_REGISTER_RESULT_QUEUE, true);
    }

    @Bean
    public Queue getAllUsersResultQueue() {
        return new Queue(Queues.USER_GET_ALL_RESULT_QUEUE, true);
    }

    @Bean
    public Queue getOneUserBaseResultQueue() {
        return new Queue(Queues.USER_GET_BASE_DATA_RESULT_QUEUE, true);
    }

    @Bean
    public Queue editUsernameResultQueue() {
        return new Queue(Queues.USER_EDIT_USERNAME_RESULT_QUEUE, true);
    }

    @Bean
    public Queue userDeleteResultQueue() {
        return new Queue(Queues.USER_DELETE_RESULT_QUEUE, true);
    }

    @Bean
    public Binding userRegisterBinding() {
        return BindingBuilder
                .bind(userRegisterQueue())
                .to(userExchange())
                .with(RoutingKeys.USER_REGISTER);
    }

    @Bean
    public Binding getAllUsersBinding() {
        return BindingBuilder
                .bind(getAllUsersQueue())
                .to(userExchange())
                .with(RoutingKeys.USER_GET_ALL);
    }

    @Bean
    public Binding getOneUserBaseBinding() {
        return BindingBuilder
                .bind(getOneUserBaseQueue())
                .to(userExchange())
                .with(RoutingKeys.USER_GET_BASE_DATA);
    }

    @Bean
    public Binding editUsernameBinding() {
        return BindingBuilder
                .bind(editUserNameQueue())
                .to(userExchange())
                .with(RoutingKeys.USER_EDIT_USERNAME);
    }

    @Bean
    public Binding userDeleteBinding() {
        return BindingBuilder
                .bind(userDeleteQueue())
                .to(userExchange())
                .with(RoutingKeys.USER_DELETE);
    }

    @Bean
    public Binding userRegisterResultBinding() {
        return BindingBuilder
                .bind(userRegisterResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.USER_RESULT_REGISTER);
    }

    @Bean
    public Binding getAllUsersResultBinding() {
        return BindingBuilder
                .bind(getAllUsersResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.USER_RESULT_GET_ALL);
    }

    @Bean
    public Binding getOneUserBaseResultBinding() {
        return BindingBuilder
                .bind(getOneUserBaseResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.USER_RESULT_GET_BASE_DATA);
    }

    @Bean
    public Binding editUsernameResultBinding() {
        return BindingBuilder
                .bind(editUsernameResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.USER_RESULT_EDIT_USERNAME);
    }

    @Bean
    public Binding userDeleteResultBinding() {
        return BindingBuilder
                .bind(userDeleteResultQueue())
                .to(userResponseExchange())
                .with(RoutingKeys.USER_RESULT_DELETE);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());

        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        return new Jackson2JsonMessageConverter(mapper);
    }
}