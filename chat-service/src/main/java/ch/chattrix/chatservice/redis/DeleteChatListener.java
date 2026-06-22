package ch.chattrix.chatservice.redis;

import ch.chattrix.chatservice.model.Chat;
import ch.chattrix.chatservice.repository.ChatRepository;
import ch.chattrix.chatservice.repository.MessageRepository;
import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.ChatDeleteEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class DeleteChatListener implements MessageListener {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message redisMessage, byte[] pattern) {
        try {

            String body = new String(redisMessage.getBody(), StandardCharsets.UTF_8);

            ChatDeleteEvent event =
                    objectMapper.readValue(body, ChatDeleteEvent.class);

            UUID chatUuid = event.getChatUuid();
            UUID userUuid = event.getUserUuid();

            Optional<Chat> optionalChat =
                    chatRepository.findByChatUuid(chatUuid);

            if (optionalChat.isEmpty()) {
                return;
            }

            Chat chat = optionalChat.get();

            if (chat.getMemberUuids() == null ||
                    !chat.getMemberUuids().contains(userUuid)) {
                return;
            }

            chatRepository.delete(chat);
            messageRepository.deleteByChatUuid(chatUuid);

            redisTemplate.convertAndSend(
                    RedisChannels.CHAT_DELETED,
                    objectMapper.writeValueAsString(event)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}