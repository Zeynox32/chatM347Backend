package ch.chattrix.chatservice.redis;

import ch.chattrix.chatservice.model.ChatMessage;
import ch.chattrix.chatservice.repository.ChatRepository;
import ch.chattrix.chatservice.repository.MessageRepository;
import ch.chattrix.shared.dto.MessageResponse;
import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.MessageSendEvent;
import ch.chattrix.shared.redis.event.MessageSentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class MessageSendListener implements MessageListener {

    private final MessageRepository repository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChatRepository chatRepository;

    @Override
    public void onMessage(Message redisChatMessage, byte[] pattern) {
        try {

            MessageSendEvent event =
                    objectMapper.readValue(redisChatMessage.getBody(), MessageSendEvent.class);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessageUuid(UUID.randomUUID());
            chatMessage.setChatUuid(event.getChatUuid());
            chatMessage.setSenderUuid(event.getSenderUuid());
            chatMessage.setContent(event.getContent());
            chatMessage.setCreatedAt(new Date());

            ChatMessage saved = repository.save(chatMessage);

            List<UUID> memberUuids = chatRepository.findByChatUuid(event.getChatUuid()).get().getMemberUuids();

            MessageResponse response = new MessageResponse();
            response.setMessageUuid(saved.getMessageUuid());
            response.setChatUuid(saved.getChatUuid());
            response.setSenderUuid(saved.getSenderUuid());
            response.setContent(saved.getContent());
            response.setCreatedAt(saved.getCreatedAt());

            MessageSentEvent sent = MessageSentEvent.builder()
                    .message(response)
                    .memberUuids(memberUuids)
                    .build();

            redisTemplate.convertAndSend(
                    RedisChannels.MESSAGE_SENT,
                    objectMapper.writeValueAsString(sent)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}