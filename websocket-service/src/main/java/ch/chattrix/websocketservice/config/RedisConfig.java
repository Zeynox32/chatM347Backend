package ch.chattrix.websocketservice.config;

import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.websocketservice.redis.*;
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
    public ChannelTopic chatReceivedTopic() {
        return new ChannelTopic(RedisChannels.CHAT_RECEIVED);
    }

    @Bean
    public ChannelTopic chatEditedTopic() {
        return new ChannelTopic(RedisChannels.CHAT_EDITED);
    }

    @Bean
    public ChannelTopic chatDeletedTopic() {
        return new ChannelTopic(RedisChannels.CHAT_DELETED);
    }

    @Bean
    public ChannelTopic messageSentTopic() {
        return new ChannelTopic(RedisChannels.MESSAGE_SENT);
    }

    @Bean
    public ChannelTopic chatMessagesReceivedTopic() {
        return new ChannelTopic(RedisChannels.CHAT_MESSAGES_RECEIVED);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory factory,
            ChatCreatedListener chatCreatedListener,
            ChatsReceivedListener chatsReceivedListener,
            ChatReceivedListener chatReceivedListener,
            ChatEditedListener chatEditedListener,
            ChatDeletedListener chatDeletedListener,
            MessageSentListener messageSentListener,
            ChatMessagesReceivedListener chatMessagesReceivedListener
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

        container.addMessageListener(
                chatReceivedListener,
                chatReceivedTopic()
        );

        container.addMessageListener(
                chatEditedListener,
                chatEditedTopic()
        );

        container.addMessageListener(
                chatDeletedListener,
                chatDeletedTopic()
        );

        container.addMessageListener(
                messageSentListener,
                messageSentTopic()
        );

        container.addMessageListener(
                chatMessagesReceivedListener,
                chatMessagesReceivedTopic()
        );

        return container;
    }
}