package ch.chattrix.websocketservice.redis;

import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.ChatMessagesGetEvent;
import ch.chattrix.shared.redis.event.MessageSendEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessagePublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(MessageSendEvent event) {
        try {
            redisTemplate.convertAndSend(
                    RedisChannels.MESSAGE_SEND,
                    objectMapper.writeValueAsString(event)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getChatMessages(ChatMessagesGetEvent event) {
        try {
            redisTemplate.convertAndSend(
                    RedisChannels.CHAT_MESSAGES_GET,
                    objectMapper.writeValueAsString(event)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}