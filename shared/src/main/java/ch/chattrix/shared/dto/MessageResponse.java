package ch.chattrix.shared.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class MessageResponse {
    private UUID messageUuid;
    private UUID chatUuid;
    private UUID senderUuid;
    private String content;
    private Date createdAt;
}
