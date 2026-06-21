package ch.chattrix.websocketservice.redis;

import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.ChatCreateEvent;
import ch.chattrix.shared.redis.event.GetChatsEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void createChat(ChatCreateEvent event) {
        try {
            redisTemplate.convertAndSend(
                    RedisChannels.CHAT_CREATE,
                    objectMapper.writeValueAsString(event)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getChats(GetChatsEvent event) {
        try {
            redisTemplate.convertAndSend(
                    RedisChannels.CHATS_GET,
                    objectMapper.writeValueAsString(event)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}