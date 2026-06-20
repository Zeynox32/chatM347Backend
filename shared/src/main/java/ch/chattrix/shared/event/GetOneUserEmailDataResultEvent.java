package ch.chattrix.shared.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetOneUserEmailDataResultEvent extends BasicRabbitMqResultEvent {
    private String email;
}
