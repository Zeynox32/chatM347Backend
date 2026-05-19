package org.example.templatebackend.chat.dto;

import org.example.templatebackend.chat.model.Member;

import java.util.List;

public record AddChatDTO(List<Member> members, String name) {
}
