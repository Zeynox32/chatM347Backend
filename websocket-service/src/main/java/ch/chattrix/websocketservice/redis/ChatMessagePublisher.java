package ch.chattrix.websocketservice.redis;

import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.*;
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

    public void getChats(ChatsGetEvent event) {
        try {
            redisTemplate.convertAndSend(
                    RedisChannels.CHATS_GET,
                    objectMapper.writeValueAsString(event)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getChat(ChatGetEvent event) {
        try {
            redisTemplate.convertAndSend(
                    RedisChannels.CHAT_GET,
                    objectMapper.writeValueAsString(event)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void editChat(ChatEditEvent event) {
        try {
            redisTemplate.convertAndSend(
                    RedisChannels.CHAT_EDIT,
                    objectMapper.writeValueAsString(event)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteChat(ChatDeleteEvent event) {
        try {
            redisTemplate.convertAndSend(
                    RedisChannels.CHAT_DELETE,
                    objectMapper.writeValueAsString(event)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}