package org.example.templatebackend.chat.service;

import org.example.templatebackend.chat.dto.AddChatDTO;
import org.example.templatebackend.chat.dto.ChatSummaryDto;
import org.example.templatebackend.chat.dto.SendMessageDTO;
import org.example.templatebackend.chat.model.Chat;
import org.example.templatebackend.chat.model.Member;
import org.example.templatebackend.chat.model.Message;
import org.example.templatebackend.chat.model.Meta;
import org.example.templatebackend.chat.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat getChat(String chatId, int userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat nicht gefunden mit ID: " + chatId));

        System.out.println(userId);

        if (chat.getMembers() == null || chat.getMembers().stream().noneMatch(member -> member.getMembers_id() == userId)) {
            throw new RuntimeException("User " + userId + " does not have access to chat " + chatId);
        }

        return chat;
    }

    public List<ChatSummaryDto> getAllMetadata(int userId) {
        List<Chat> chat = chatRepository.findAllByUserId(userId);

        return chat.stream()
                .map(c -> new ChatSummaryDto(c.getChatId(), c.getMeta().getName()))
                .toList();
    }

    public Chat addChat(AddChatDTO newChat, int userId ) {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if (newChat.members() == null || newChat.members().stream().noneMatch(member -> member.getMembers_id() == userId)) {
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
            System.out.println(userId);
            throw new RuntimeException("User " + userId + " is not a member of the new chat.");
        }

        List<Message> messages = List.of();

        Meta meta = new Meta();
        meta.setName(newChat.name());
        meta.setCreatedAt(new Date());

        Chat chat = new Chat();
        chat.setMeta(meta);
        chat.setMembers(newChat.members());
        chat.setMessages(messages);

        return chatRepository.save(chat);
    }

    public Chat sendMessage(SendMessageDTO messageDto, int userId) {

        Chat chat = chatRepository.findById(messageDto.chatId())
                .orElseThrow(() -> new RuntimeException("Chat nicht gefunden mit ID: " + messageDto.chatId()));

        if (chat.getMembers() == null || chat.getMembers().stream().noneMatch(member -> member.getMembers_id() == userId)) {
            throw new RuntimeException("User " + userId + " does not have access to chat " + messageDto.chatId());
        }

        Message message = new Message();
        message.setMessageId(chat.getMessages().size() + 1);
        message.setSenderId(userId);
        message.setText(messageDto.text());
        message.setTimestamp(new Date());

        chat.getMessages().add(message);

        return chatRepository.save(chat);
    }
}
