package ch.chattrix.chatservice.config;

import ch.chattrix.chatservice.redis.*;
import ch.chattrix.shared.redis.channel.RedisChannels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfig {

    @Bean
    public ChannelTopic chatCreateTopic() {
        return new ChannelTopic(RedisChannels.CHAT_CREATE);
    }

    @Bean
    public ChannelTopic chatCreatedTopic() {
        return new ChannelTopic(RedisChannels.CHAT_CREATED);
    }

    @Bean
    public ChannelTopic chatsGetTopic() {
        return new ChannelTopic(RedisChannels.CHATS_GET);
    }

    @Bean
    public ChannelTopic chatsReceivedTopic() {
        return new ChannelTopic(RedisChannels.CHATS_RECEIVED);
    }

    @Bean
    public ChannelTopic chatGetTopic() {
        return new ChannelTopic(RedisChannels.CHAT_GET);
    }

    @Bean
    public ChannelTopic chatReceivedTopic() {
        return new ChannelTopic(RedisChannels.CHAT_RECEIVED);
    }

    @Bean
    public ChannelTopic chatEditTopic() {
        return new ChannelTopic(RedisChannels.CHAT_EDIT);
    }

    @Bean
    public ChannelTopic chatEditedTopic() {
        return new ChannelTopic(RedisChannels.CHAT_EDITED);
    }

    @Bean
    public ChannelTopic chatDeleteTopic() {
        return new ChannelTopic(RedisChannels.CHAT_DELETE);
    }

    @Bean
    public ChannelTopic chatDeletedTopic() {
        return new ChannelTopic(RedisChannels.CHAT_DELETED);
    }

    @Bean
    public ChannelTopic chatMessagesGetTopic() {
        return new ChannelTopic(RedisChannels.CHAT_MESSAGES_GET);
    }

    @Bean
    public ChannelTopic chatMessagesReceivedTopic() {
        return new ChannelTopic(RedisChannels.CHAT_MESSAGES_RECEIVED);
    }

    @Bean
    public ChannelTopic messageSendTopic() {
        return new ChannelTopic(RedisChannels.MESSAGE_SEND);
    }

    @Bean
    public ChannelTopic messageSentTopic() {
        return new ChannelTopic(RedisChannels.MESSAGE_SENT);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory factory,
            ChatCreateListener chatCreateListener,
            GetChatsListener getChatsListener,
            GetChatListener getChatListener,
            EditChatListener editChatListener,
            DeleteChatListener deleteChatListener,
            MessageSendListener messageSendListener,
            GetChatMessagesListener getChatMessagesListener
    ) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);

        container.addMessageListener(chatCreateListener, chatCreateTopic());
        container.addMessageListener(getChatsListener, chatsGetTopic());
        container.addMessageListener(getChatListener, chatGetTopic());
        container.addMessageListener(editChatListener, chatEditTopic());
        container.addMessageListener(deleteChatListener, chatDeleteTopic());
        container.addMessageListener(getChatMessagesListener, chatMessagesGetTopic());
        container.addMessageListener(messageSendListener, messageSendTopic());

        return container;
    }
}