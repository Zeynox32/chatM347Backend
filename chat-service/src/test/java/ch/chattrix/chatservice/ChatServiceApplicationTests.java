package ch.chattrix.chatservice;

import ch.chattrix.chatservice.redis.MessageSendListener;
import ch.chattrix.chatservice.repository.ChatRepository;
import ch.chattrix.chatservice.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ChatServiceApplicationTests {

    @MockBean
    private ChatRepository chatRepository;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private MessageSendListener messageSendListener;

    @MockBean
    private RedisMessageListenerContainer redisContainer;

    @Test
    void contextLoads() {
    }
}