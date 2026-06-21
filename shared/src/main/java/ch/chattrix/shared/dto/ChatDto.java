package ch.chattrix.shared.dto;

import ch.chattrix.shared.enums.ChatType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ChatDto {
    private UUID chatUuid;
    private UUID creatorUuid;
    private ChatType chatType;
    private String name;
    private Date createdAt;
    private List<UUID> memberUuids;
}
