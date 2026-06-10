package ch.bbw.chattrix.repository;

import ch.bbw.chattrix.entity.mongodb.ChatGroup;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupRepository extends MongoRepository<ChatGroup, String> {
    List<ChatGroup> findByMembersMemberId(Integer memberId);
}
