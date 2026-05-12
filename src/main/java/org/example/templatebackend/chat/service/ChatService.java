package org.example.templatebackend.chat.service;

import org.example.templatebackend.chat.model.Chat;
import org.example.templatebackend.chat.repository.ChatRepository;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat getChat(int chatId, int userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat nicht gefunden mit ID: " + chatId));

        if (chat.getMembers() == null || chat.getMembers().stream().noneMatch(member -> member.getId() == userId)) {
            throw new RuntimeException("User " + userId + " does not have access to chat " + chatId);
        }

        return chat;
    }
}
