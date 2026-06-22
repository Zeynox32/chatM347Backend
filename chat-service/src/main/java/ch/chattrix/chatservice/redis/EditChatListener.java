package ch.chattrix.chatservice.redis;

import ch.chattrix.chatservice.model.Chat;
import ch.chattrix.chatservice.repository.ChatRepository;
import ch.chattrix.shared.dto.ChatResponse;
import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.ChatEditEvent;
import ch.chattrix.shared.redis.event.ChatEditedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class EditChatListener implements MessageListener {

    private final ChatRepository chatRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message redisMessage, byte[] pattern) {
        try {
            String body = new String(redisMessage.getBody());

            ChatEditEvent event =
                    objectMapper.readValue(body, ChatEditEvent.class);

            UUID chatUuid = event.getChatUuid();
            UUID userUuid = event.getUserUuid();

            Optional<Chat> optionalChat = chatRepository.findByChatUuid(chatUuid);

            if (optionalChat.isEmpty()) {
                return;
            }

            Chat chat = optionalChat.get();

            if (chat.getMemberUuids() == null ||
                    !chat.getMemberUuids().contains(userUuid)) {
                return;
            }

            if (event.getName() != null) {
                chat.setName(event.getName());
            }

            if (event.getMemberUuids() != null && !event.getMemberUuids().isEmpty()) {
                chat.setMemberUuids(event.getMemberUuids());
            }

            Chat saved = chatRepository.save(chat);

            ChatResponse chatResponse = new ChatResponse();
            chatResponse.setChatUuid(saved.getChatUuid());
            chatResponse.setName(saved.getName());
            chatResponse.setCreatorUuid(saved.getCreatorUuid());
            chatResponse.setChatType(saved.getChatType());
            chatResponse.setMemberUuids(saved.getMemberUuids());
            chatResponse.setCreatedAt(saved.getCreatedAt());

            ChatEditedEvent editedEvent = ChatEditedEvent.builder()
                    .chat(chatResponse)
                    .build();

            redisTemplate.convertAndSend(
                    RedisChannels.CHAT_EDITED,
                    objectMapper.writeValueAsString(editedEvent)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}