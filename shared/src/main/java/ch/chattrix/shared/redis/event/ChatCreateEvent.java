package ch.chattrix.shared.redis.event;

import ch.chattrix.shared.enums.ChatType;
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
public class ChatCreateEvent {
    private String name;
    private ChatType chatType;
    private UUID creatorUuid;
    private List<UUID> memberUuids;
    private long timestamp;
}