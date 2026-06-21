package ch.chattrix.websocketservice.config;

import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.websocketservice.redis.ChatCreatedListener;
import ch.chattrix.websocketservice.redis.ChatsReceivedListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfig {

    @Bean
    public ChannelTopic chatCreatedTopic() {
        return new ChannelTopic(RedisChannels.CHAT_CREATED);
    }

    @Bean
    public ChannelTopic chatsReceivedTopic() {
        return new ChannelTopic(RedisChannels.CHATS_RECEIVED);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory factory,
            ChatCreatedListener chatCreatedListener,
            ChatsReceivedListener chatsReceivedListener
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);

        container.addMessageListener(
                chatCreatedListener,
                chatCreatedTopic()
        );

        container.addMessageListener(
                chatsReceivedListener,
                chatsReceivedTopic()
        );

        return container;
    }
}