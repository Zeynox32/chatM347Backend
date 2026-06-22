package ch.chattrix.chatservice.redis;

import ch.chattrix.chatservice.model.Chat;
import ch.chattrix.chatservice.repository.ChatRepository;
import ch.chattrix.shared.dto.ChatResponse;
import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.ChatGetEvent;
import ch.chattrix.shared.redis.event.ChatReceivedEvent;
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

            ChatResponse chatResponse = new ChatResponse();
            chatResponse.setChatUuid(chat.getChatUuid());
            chatResponse.setChatType(chat.getChatType());
            chatResponse.setName(chat.getName());
            chatResponse.setMemberUuids(chat.getMemberUuids());
            chatResponse.setCreatorUuid(chat.getCreatorUuid());
            chatResponse.setCreatedAt(chat.getCreatedAt());

            ChatReceivedEvent response = ChatReceivedEvent.builder()
                    .userUuid(userUuid)
                    .chat(chatResponse)
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