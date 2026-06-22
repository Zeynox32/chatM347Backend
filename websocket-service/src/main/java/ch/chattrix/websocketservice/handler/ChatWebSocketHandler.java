package ch.chattrix.websocketservice.handler;

import ch.chattrix.shared.enums.ChatType;
import ch.chattrix.shared.redis.event.*;
import ch.chattrix.websocketservice.redis.ChatMessagePublisher;
import ch.chattrix.websocketservice.redis.MessagePublisher;
import ch.chattrix.websocketservice.registry.WebSocketSessionRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatMessagePublisher publisher;
    private final WebSocketSessionRegistry sessionRegistry;
    private final MessagePublisher messagePublisher;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        UUID userUuid = (UUID) session.getAttributes().get("userUuid");

        if (userUuid != null) {
            sessionRegistry.register(userUuid, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        UUID userUuid = (UUID) session.getAttributes().get("userUuid");

        if (userUuid != null) {
            sessionRegistry.remove(userUuid);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        JsonNode node = objectMapper.readTree(message.getPayload());

        String eventType = node.get("type").asText();

        if ("CREATE_CHAT".equals(eventType)) {

            UUID creatorUuid = (UUID) session.getAttributes().get("userUuid");

            List<UUID> memberUuids = objectMapper.convertValue(
                    node.get("memberUuids"),
                    objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, UUID.class)
            );

            if (!memberUuids.contains(creatorUuid)) {
                memberUuids.add(creatorUuid);
            }

            ChatCreateEvent event = ChatCreateEvent.builder()
                    .name(node.get("name").asText())
                    .chatType(ChatType.valueOf(node.get("chatType").asText()))
                    .creatorUuid(creatorUuid)
                    .memberUuids(memberUuids)
                    .build();

            publisher.createChat(event);
        }

        if ("GET_CHATS".equals(eventType)) {

            UUID userUuid = (UUID) session.getAttributes().get("userUuid");

            ChatsGetEvent event = ChatsGetEvent.builder()
                    .userUuid(userUuid)
                    .build();

            publisher.getChats(event);
        }

        if ("GET_CHAT".equals(eventType)) {

            UUID userUuid = (UUID) session.getAttributes().get("userUuid");

            ChatGetEvent event = ChatGetEvent.builder()
                    .userUuid(userUuid)
                    .chatUuid(UUID.fromString(node.get("chatUuid").asText()))
                    .build();

            publisher.getChat(event);
        }

        if ("EDIT_CHAT".equals(eventType)) {

            UUID userUuid = (UUID) session.getAttributes().get("userUuid");

            List<UUID> memberUuids = objectMapper.convertValue(
                    node.get("memberUuids"),
                    objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, UUID.class)
            );

            ChatEditEvent event = ChatEditEvent.builder()
                    .userUuid(userUuid)
                    .chatUuid(UUID.fromString(node.get("chatUuid").asText()))
                    .memberUuids(memberUuids)
                    .name(node.get("name").asText())
                    .build();

            publisher.editChat(event);
        }

        if ("DELETE_CHAT".equals(eventType)) {

            UUID userUuid = (UUID) session.getAttributes().get("userUuid");

            List<UUID> memberUuids = objectMapper.convertValue(
                    node.get("memberUuids"),
                    objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, UUID.class)
            );

            ChatDeleteEvent event = ChatDeleteEvent.builder()
                    .chatUuid(UUID.fromString(node.get("chatUuid").asText()))
                    .userUuid(userUuid)
                    .memberUuids(memberUuids)
                    .build();

            publisher.deleteChat(event);
        }

        if ("CREATE_MESSAGE".equals(eventType)) {

            UUID senderUuid = (UUID) session.getAttributes().get("userUuid");

            MessageSendEvent event = MessageSendEvent.builder()
                    .chatUuid(UUID.fromString(node.get("chatUuid").asText()))
                    .senderUuid(senderUuid)
                    .content(node.get("content").asText())
                    .build();

            messagePublisher.sendMessage(event);
        }

        if ("GET_CHAT_MESSAGES".equals(eventType)) {

            UUID userUuid = (UUID) session.getAttributes().get("userUuid");

            ChatMessagesGetEvent event = ChatMessagesGetEvent.builder()
                    .chatUuid(UUID.fromString(node.get("chatUuid").asText()))
                    .userUuid(userUuid)
                    .build();

            messagePublisher.getChatMessages(event);
        }
    }
}