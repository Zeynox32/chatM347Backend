package ch.chattrix.chatservice;

import ch.chattrix.chatservice.repository.ChatRepository;
import ch.chattrix.chatservice.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest()
@ActiveProfiles("test")
class ChatServiceApplicationTests {

    @MockBean
    private ChatRepository chatRepository;

    @MockBean
    private MessageRepository messageRepository;

    @Test
    void contextLoads() {
    }
}