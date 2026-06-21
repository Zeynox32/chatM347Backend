package ch.chattrix.chatservice.redis;

import ch.chattrix.chatservice.model.Chat;
import ch.chattrix.chatservice.repository.ChatRepository;
import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.ChatDeleteEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteChatListener implements MessageListener {

    private final ChatRepository chatRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message redisMessage, byte[] pattern) {
        try {
            String body = new String(redisMessage.getBody());

            ChatDeleteEvent event =
                    objectMapper.readValue(body, ChatDeleteEvent.class);

            UUID chatUuid = event.getChatUuid();

            Optional<Chat> optionalChat = chatRepository.findByChatUuid(chatUuid);

            if (optionalChat.isEmpty()) {
                return;
            }

            Chat chat = optionalChat.get();

            chatRepository.delete(chat);

            redisTemplate.convertAndSend(
                    RedisChannels.CHAT_DELETED,
                    objectMapper.writeValueAsString(event)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}