package org.example.templatebackend.chat.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Meta {
    private String name;
    private Date createdAt;
}
