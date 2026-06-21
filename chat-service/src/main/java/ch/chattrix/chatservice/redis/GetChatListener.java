package ch.chattrix.chatservice.redis;

import ch.chattrix.chatservice.model.Chat;
import ch.chattrix.chatservice.repository.ChatRepository;
import ch.chattrix.shared.dto.ChatDto;
import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.ChatGetEvent;
import ch.chattrix.shared.redis.event.ChatReceivedEvent;
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
public class GetChatListener implements MessageListener {

    private final ChatRepository chatRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message redisMessage, byte[] pattern) {
        try {
            String body = new String(redisMessage.getBody());

            ChatGetEvent event =
                    objectMapper.readValue(body, ChatGetEvent.class);

            UUID userUuid = event.getUserUuid();

            Optional<Chat> chatOpt =
                    chatRepository.findByChatUuid(event.getChatUuid());

            if (chatOpt.isEmpty()) {
                return;
            }

            Chat chat = chatOpt.get();

            if (chat.getMemberUuids() == null ||
                    !chat.getMemberUuids().contains(userUuid)) {
                return;
            }

            ChatDto chatDto = new ChatDto();
            chatDto.setChatUuid(chat.getChatUuid());
            chatDto.setChatType(chat.getChatType());
            chatDto.setName(chat.getName());
            chatDto.setMemberUuids(chat.getMemberUuids());
            chatDto.setCreatorUuid(chat.getCreatorUuid());
            chatDto.setCreatedAt(chat.getCreatedAt());

            ChatReceivedEvent response = ChatReceivedEvent.builder()
                    .userUuid(userUuid)
                    .chat(chatDto)
                    .build();

            redisTemplate.convertAndSend(
                    RedisChannels.CHAT_RECEIVED,
                    objectMapper.writeValueAsString(response)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}