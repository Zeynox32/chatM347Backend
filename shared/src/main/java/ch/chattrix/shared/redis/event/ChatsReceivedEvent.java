package ch.chattrix.shared.redis.event;

import ch.chattrix.shared.dto.ChatResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatsReceivedEvent {
    private UUID userUuid;
    private List<ChatResponse> chats;
}