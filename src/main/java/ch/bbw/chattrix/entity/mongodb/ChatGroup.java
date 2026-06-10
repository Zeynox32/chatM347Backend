package ch.bbw.chattrix.entity.mongodb;

import ch.bbw.chattrix.entity.Member;
import ch.bbw.chattrix.entity.Message;
import ch.bbw.chattrix.entity.Meta;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("ChatGroup")
@Getter
@Setter
public class ChatGroup {
    @Id
    private String chatGroupId;
    private Meta meta;
    private List<Member> members;
    private List<Message> messages;
}
