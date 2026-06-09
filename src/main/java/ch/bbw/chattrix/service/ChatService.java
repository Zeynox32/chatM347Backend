package ch.bbw.chattrix.service;

import ch.bbw.chattrix.dto.chat.ChatGroupRequest;
import ch.bbw.chattrix.dto.chat.ChatGroupResponse;
import ch.bbw.chattrix.dto.chat.ChatRequest;
import ch.bbw.chattrix.entity.mongodb.Chat;
import ch.bbw.chattrix.entity.Message;
import ch.bbw.chattrix.entity.Meta;
import ch.bbw.chattrix.repository.ChatRepository;
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

    public List<ChatGroupResponse> getAllMetadata(int userId) {
        List<Chat> chat = chatRepository.findAllByUserId(userId);

        return chat.stream()
                .map(c -> new ChatGroupResponse(c.getChatId(), c.getMeta().getName()))
                .toList();
    }

    public Chat addChat(ChatGroupRequest newChat, int userId ) {
        if (newChat.members() == null || newChat.members().stream().noneMatch(member -> member.getMembers_id() == userId)) {
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

    public Chat sendMessage(ChatRequest messageDto, int userId) {

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
