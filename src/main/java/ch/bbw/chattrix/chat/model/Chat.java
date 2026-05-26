package ch.bbw.chattrix.chat.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("Chat")
@Getter
@Setter
public class Chat {
    @Id
    private String chatId;
    private Meta meta;
    private List<Member> members;
    private List<Message> messages;
}
