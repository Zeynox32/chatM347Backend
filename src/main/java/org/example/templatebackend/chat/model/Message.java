package org.example.templatebackend.chat.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Message {
    private int id;
    public String text;
    public int senderId;
    public Date timestamp;
}
