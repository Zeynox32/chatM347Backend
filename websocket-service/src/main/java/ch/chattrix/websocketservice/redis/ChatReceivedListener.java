package ch.chattrix.websocketservice.redis;

import ch.chattrix.shared.redis.event.ChatReceivedEvent;
import ch.chattrix.websocketservice.registry.WebSocketSessionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class ChatReceivedListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final WebSocketSessionRegistry sessionRegistry;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ChatReceivedEvent event =
                    objectMapper.readValue(message.getBody(), ChatReceivedEvent.class);

            if (event.getUserUuid() == null) {
                return;
            }

            WebSocketSession session =
                    sessionRegistry.get(event.getUserUuid());

            if (session == null || !session.isOpen()) {
                return;
            }

            session.sendMessage(
                    new TextMessage(objectMapper.writeValueAsString(event))
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}