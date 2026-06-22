package ch.chattrix.chatservice.redis;

import ch.chattrix.chatservice.model.Chat;
import ch.chattrix.chatservice.repository.ChatRepository;
import ch.chattrix.shared.dto.ChatResponse;
import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.ChatsGetEvent;
import ch.chattrix.shared.redis.event.ChatsReceivedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetChatsListener implements MessageListener {

    private final ChatRepository chatRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message redisMessage, byte[] pattern) {
        try {
            String body = new String(redisMessage.getBody());

            ChatsGetEvent event = objectMapper.readValue(body, ChatsGetEvent.class);

            List<Chat> chats = chatRepository.findByMemberUuidsContaining(event.getUserUuid());

            List<ChatResponse> chatResponses = chats.stream().map(chat -> {
                ChatResponse dto = new ChatResponse();
                dto.setChatUuid(chat.getChatUuid());
                dto.setName(chat.getName());
                dto.setCreatorUuid(chat.getCreatorUuid());
                dto.setChatType(chat.getChatType());
                dto.setMemberUuids(chat.getMemberUuids());
                dto.setCreatedAt(chat.getCreatedAt());
                return dto;
            }).toList();

            ChatsReceivedEvent response = ChatsReceivedEvent.builder()
                    .userUuid(event.getUserUuid())
                    .chats(chatResponses)
                    .build();

            redisTemplate.convertAndSend(
                    RedisChannels.CHATS_RECEIVED,
                    objectMapper.writeValueAsString(response)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}