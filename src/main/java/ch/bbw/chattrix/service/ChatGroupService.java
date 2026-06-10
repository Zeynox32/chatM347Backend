package ch.bbw.chattrix.service;

import ch.bbw.chattrix.dto.chatgroup.ChatGroupRequest;
import ch.bbw.chattrix.dto.chatgroup.ChatGroupResponse;
import ch.bbw.chattrix.dto.message.MessageRequest;
import ch.bbw.chattrix.entity.Member;
import ch.bbw.chattrix.entity.mariadb.User;
import ch.bbw.chattrix.entity.mongodb.ChatGroup;
import ch.bbw.chattrix.entity.Message;
import ch.bbw.chattrix.entity.Meta;
import ch.bbw.chattrix.repository.ChatGroupRepository;
import ch.bbw.chattrix.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ChatGroupService {
    private final ChatGroupRepository chatGroupRepository;
    private final UserRepository userRepository;

    public ChatGroupService(ChatGroupRepository chatGroupRepository, UserRepository userRepository) {
        this.chatGroupRepository = chatGroupRepository;
        this.userRepository = userRepository;
    }

    public ChatGroup getChatGroup(String chatId, int userId) {
        ChatGroup chatGroup = chatGroupRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("ChatGroup nicht gefunden mit ID: " + chatId));

        if (chatGroup.getMembers() == null || chatGroup.getMembers().stream().noneMatch(member -> member.getMemberId() == userId)) {
            throw new RuntimeException("User " + userId + " does not have access to chatGroup " + chatId);
        }

        return chatGroup;
    }

    public List<ChatGroupResponse> getAllMetadata(int userId) {
        List<ChatGroup> chatGroup = chatGroupRepository.findByMembersMemberId(userId);

        return chatGroup.stream()
                .map(c -> new ChatGroupResponse(c.getChatGroupId(), c.getMeta().getName()))
                .toList();
    }

    public void deleteChatGroup(String chatGroupId, int userId) {

        ChatGroup chatGroup = chatGroupRepository.findById(chatGroupId)
                .orElseThrow(() ->
                        new RuntimeException("ChatGroup not found!"));

        boolean isMember = chatGroup.getMembers().stream()
                .anyMatch(member -> member.getMemberId() == userId);

        if (!isMember) {
            throw new RuntimeException("You are not a member of this chat group!");
        }

        chatGroupRepository.delete(chatGroup);
    }

    public ChatGroup createChatGroup(ChatGroupRequest newChat, int userId) {

        Optional<User> currentUser = userRepository.findById(userId);
        if (currentUser.isEmpty()) {
            throw new RuntimeException("User with userId: " + userId + " was not found!");
        }

        Member creator = new Member();
        creator.setMemberId(userId);
        creator.setName(currentUser.get().getDisplayName());

        List<Member> members = new ArrayList<>();

        if (newChat.members() != null) {
            members.addAll(newChat.members());
        }

        boolean alreadyExists = members.stream()
                .anyMatch(m -> m.getMemberId() == userId);

        if (!alreadyExists) {
            members.add(creator);
        }

        Meta meta = new Meta();
        meta.setName(newChat.name());
        meta.setCreatedAt(new Date());

        ChatGroup chatGroup = new ChatGroup();
        chatGroup.setMeta(meta);
        chatGroup.setMembers(members);
        chatGroup.setMessages(new ArrayList<>());

        return chatGroupRepository.save(chatGroup);
    }

    public ChatGroup sendMessage(MessageRequest messageDto, int userId) {

        ChatGroup chatGroup = chatGroupRepository.findById(messageDto.chatId())
                .orElseThrow(() -> new RuntimeException("ChatGroup nicht gefunden mit ID: " + messageDto.chatId()));

        if (chatGroup.getMembers() == null || chatGroup.getMembers().stream().noneMatch(member -> member.getMemberId() == userId)) {
            throw new RuntimeException("User " + userId + " does not have access to chatGroup " + messageDto.chatId());
        }

        Message message = new Message();
        message.setMessageId(chatGroup.getMessages().size() + 1);
        message.setSenderId(userId);
        message.setText(messageDto.text());
        message.setTimestamp(new Date());

        chatGroup.getMessages().add(message);

        return chatGroupRepository.save(chatGroup);
    }
}
