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
public class ChatDeleteEvent {
    private UUID chatUuid;
    private List<UUID> memberUuids;
}
