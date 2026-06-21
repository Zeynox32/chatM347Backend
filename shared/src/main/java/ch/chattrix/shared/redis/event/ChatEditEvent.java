package ch.chattrix.shared.redis.event;

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
public class ChatEditEvent {
    private UUID userUuid;
    private UUID chatUuid;
    private String name;
    private List<UUID> memberUuids;
    private long timestamp;
}
