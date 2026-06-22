package ch.chattrix.websocketservice.redis;

import ch.chattrix.shared.redis.event.ChatMessagesReceivedEvent;
import ch.chattrix.websocketservice.registry.WebSocketSessionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatMessagesReceivedListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final WebSocketSessionRegistry sessionRegistry;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {

            ChatMessagesReceivedEvent event =
                    objectMapper.readValue(message.getBody(), ChatMessagesReceivedEvent.class);

            String payload = objectMapper.writeValueAsString(event);

            for (UUID userUuid : event.getChat().getMemberUuids()) {

                WebSocketSession session = sessionRegistry.get(userUuid);

                if (session != null && session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
