package ch.chattrix.chatservice.repository;

import ch.chattrix.chatservice.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends MongoRepository<Chat, String> {
    List<Chat> findByMemberUuidsContaining(UUID userUuid);

    Optional<Chat> findByChatUuid(UUID chatUuid);
}