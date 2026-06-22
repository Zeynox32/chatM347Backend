package ch.chattrix.shared.redis.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEvent {
    private UUID messageUuid;
    private UUID chatUuid;
    private UUID senderUuid;
    private String senderUsername;
    private String content;
}
