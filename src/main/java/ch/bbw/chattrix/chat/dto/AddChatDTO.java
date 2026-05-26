package ch.bbw.chattrix.chat.dto;

import ch.bbw.chattrix.chat.model.Member;

import java.util.List;

public record AddChatDTO(List<Member> members, String name) {
}
