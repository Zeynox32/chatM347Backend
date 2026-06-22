package ch.chattrix.websocketservice.redis;

import ch.chattrix.shared.redis.event.ChatCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatCreatedListener implements MessageListener {

    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {

            ChatCreatedEvent event =
                    objectMapper.readValue(message.getBody(), ChatCreatedEvent.class);

            if (event.getChat() == null || event.getChat().getMemberUuids() == null) {
                return;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}