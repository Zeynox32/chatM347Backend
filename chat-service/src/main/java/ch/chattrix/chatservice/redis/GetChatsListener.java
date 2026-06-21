package ch.chattrix.chatservice.redis;

import ch.chattrix.chatservice.model.Chat;
import ch.chattrix.chatservice.repository.ChatRepository;
import ch.chattrix.shared.dto.ChatDto;
import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.GetChatsEvent;
import ch.chattrix.shared.redis.event.ReceivedChatsEvent;
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

            GetChatsEvent event = objectMapper.readValue(body, GetChatsEvent.class);

            List<Chat> chats = chatRepository.findByMemberUuidsContaining(event.getUserUuid());

            List<ChatDto> chatDtos = chats.stream().map(chat -> {
                ChatDto dto = new ChatDto();
                dto.setChatUuid(chat.getChatUuid());
                dto.setName(chat.getName());
                dto.setCreatorUuid(chat.getCreatorUuid());
                dto.setChatType(chat.getChatType());
                dto.setMemberUuids(chat.getMemberUuids());
                dto.setCreatedAt(chat.getCreatedAt());
                return dto;
            }).toList();

            ReceivedChatsEvent response = ReceivedChatsEvent.builder()
                    .userUuid(event.getUserUuid())
                    .chats(chatDtos)
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