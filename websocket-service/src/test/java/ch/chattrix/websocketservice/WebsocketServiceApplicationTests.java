package ch.chattrix.websocketservice;

import ch.chattrix.shared.utils.JwtValidator;
import ch.chattrix.websocketservice.config.JwtHandshakeInterceptor;
import ch.chattrix.websocketservice.redis.ChatMessagePublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class WebsocketServiceApplicationTests {

    @MockBean
    private JwtValidator jwtValidator;

    @MockBean
    private JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @MockBean
    private ChatMessagePublisher chatMessagePublisher;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Test
    void contextLoads() {
    }
}