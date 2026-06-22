package ch.chattrix.shared.redis.event;

import ch.chattrix.shared.dto.ChatResponse;
import ch.chattrix.shared.dto.MessageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessagesReceivedEvent {
    private ChatResponse chat;
    private List<MessageResponse> messages;
}
