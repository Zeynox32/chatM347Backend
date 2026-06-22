package ch.chattrix.shared.redis.event;

import ch.chattrix.shared.dto.ChatResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatReceivedEvent {
    private UUID userUuid;
    private ChatResponse chat;
}
