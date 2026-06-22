package ch.chattrix.shared.rabbitmq.event;

import ch.chattrix.shared.types.UserAnonymData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllUsersResultEvent extends BasicRabbitMqResultEvent{
    private List<UserAnonymData> users;
}
