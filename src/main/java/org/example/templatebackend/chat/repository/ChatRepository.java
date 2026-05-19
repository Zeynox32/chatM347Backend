package org.example.templatebackend.chat.repository;

import org.example.templatebackend.chat.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    @Query(value = "{'members.members_id' : ?0}")
    List<Chat> findAllByUserId(Integer membersId);
}
