package ch.chattrix.gatewayservice;

import ch.chattrix.shared.utils.JwtValidator;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
		properties = {
				"FRONTEND_URL=http://localhost:5173"
		}
)
@ActiveProfiles("test")
class GatewayServiceApplicationTests {

	@MockBean
	private JwtValidator jwtValidator;

	@MockBean
	private ConnectionFactory connectionFactory;

	@MockBean
	private RabbitTemplate rabbitTemplate;

	@Test
	void contextLoads() {
	}
}