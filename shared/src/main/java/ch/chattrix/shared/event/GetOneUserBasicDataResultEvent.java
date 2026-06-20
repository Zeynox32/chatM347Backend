package ch.chattrix.shared.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetOneUserBasicDataResultEvent extends BasicRabbitMqResultEvent {
    private UUID userUuid;
    private String username;
    private Date createdAt;
}
