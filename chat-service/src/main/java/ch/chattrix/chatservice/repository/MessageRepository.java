package ch.chattrix.chatservice.repository;

import ch.chattrix.chatservice.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatUuid(UUID chatUuid);
}