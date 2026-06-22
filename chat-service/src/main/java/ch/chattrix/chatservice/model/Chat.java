package ch.chattrix.chatservice.model;

import ch.chattrix.shared.enums.ChatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chats")
public class Chat {
    @Id
    private String id;
    private UUID chatUuid;
    private String name;
    private ChatType chatType;
    private UUID creatorUuid;
    private Date createdAt;
    private List<UUID> memberUuids;

}