package ch.chattrix.shared.redis.event;

import ch.chattrix.shared.dto.ChatDto;
import ch.chattrix.shared.enums.ChatType;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCreatedEvent {
    private ChatDto chatDto;
    private long createdAt;
}