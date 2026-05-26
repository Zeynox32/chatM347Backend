package ch.bbw.chattrix.chat.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Message {
    private int messageId;
    public String text;
    public int senderId;
    public Date timestamp;
}
