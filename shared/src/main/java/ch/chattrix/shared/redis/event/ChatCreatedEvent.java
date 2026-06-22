package ch.chattrix.shared.redis.event;

import ch.chattrix.shared.dto.ChatResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCreatedEvent {
    private ChatResponse chat;
    private long createdAt;
}