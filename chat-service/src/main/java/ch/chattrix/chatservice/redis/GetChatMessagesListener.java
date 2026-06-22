package ch.chattrix.chatservice.redis;

import ch.chattrix.chatservice.model.Chat;
import ch.chattrix.chatservice.rabbitmq.UsernameClient;
import ch.chattrix.chatservice.repository.ChatRepository;
import ch.chattrix.chatservice.repository.MessageRepository;
import ch.chattrix.shared.dto.ChatResponse;
import ch.chattrix.shared.dto.MessageResponse;
import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.ChatMessagesGetEvent;
import ch.chattrix.shared.redis.event.ChatMessagesReceivedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class GetChatMessagesListener implements MessageListener {

    private final ChatRepository chatRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final MessageRepository messageRepository;
    private final UsernameClient usernameClient;

    @Override
    public void onMessage(Message redisMessage, byte[] pattern) {

        try {

            String body = new String(redisMessage.getBody());

            ChatMessagesGetEvent event =
                    objectMapper.readValue(body, ChatMessagesGetEvent.class);

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

            List<MessageResponse> messageResponses =
                    messageRepository.findByChatUuid(event.getChatUuid())
                            .stream()
                            .map(msg -> {
                                MessageResponse dto = new MessageResponse();
                                dto.setMessageUuid(msg.getMessageUuid());
                                dto.setChatUuid(msg.getChatUuid());
                                dto.setSenderUuid(msg.getSenderUuid());
                                dto.setContent(msg.getContent());
                                dto.setCreatedAt(msg.getCreatedAt());
                                return dto;
                            })
                            .toList();

            List<UUID> senderUuids = messageResponses.stream()
                    .map(MessageResponse::getSenderUuid)
                    .distinct()
                    .toList();

            CompletableFuture<Map<UUID, String>> usernamesFuture =
                    usernameClient.getUsernames(senderUuids);

            Map<UUID, String> usernames =
                    usernamesFuture.get();

            messageResponses.forEach(msg ->
                    msg.setUsername(usernames.get(msg.getSenderUuid()))
            );

            ChatResponse chatResponse = new ChatResponse();
            chatResponse.setChatUuid(chat.getChatUuid());
            chatResponse.setChatType(chat.getChatType());
            chatResponse.setName(chat.getName());
            chatResponse.setMemberUuids(chat.getMemberUuids());
            chatResponse.setCreatorUuid(chat.getCreatorUuid());
            chatResponse.setCreatedAt(chat.getCreatedAt());

            ChatMessagesReceivedEvent response =
                    ChatMessagesReceivedEvent.builder()
                            .chat(chatResponse)
                            .messages(messageResponses)
                            .build();

            redisTemplate.convertAndSend(
                    RedisChannels.CHAT_MESSAGES_RECEIVED,
                    objectMapper.writeValueAsString(response)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}