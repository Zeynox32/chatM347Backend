package ch.chattrix.shared.rabbitmq.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUsernamesResultEvent extends BasicRabbitMqResultEvent {
    private Map<UUID, String> usernames;
}
