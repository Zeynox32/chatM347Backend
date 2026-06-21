package ch.chattrix.websocketservice.registry;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionRegistry {

    private final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void register(UUID userUuid, WebSocketSession session) {
        sessions.put(userUuid, session);
    }

    public void remove(UUID userUuid) {
        sessions.remove(userUuid);
    }

    public WebSocketSession get(UUID userUuid) {
        return sessions.get(userUuid);
    }
}