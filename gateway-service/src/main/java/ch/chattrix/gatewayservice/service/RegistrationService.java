package ch.chattrix.gatewayservice.service;

import ch.chattrix.gatewayservice.aggregator.RegistrationAggregator;
import ch.chattrix.gatewayservice.rabbitmq.RabbitCommandPublisher;
import ch.chattrix.shared.command.user.AuthenticationRegisterCommand;
import ch.chattrix.shared.command.user.UserProfileCommand;
import ch.chattrix.shared.dto.user.RegisterUserRequest;
import ch.chattrix.shared.response.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RegistrationService {

    private final RabbitCommandPublisher publisher;
    private final RegistrationAggregator aggregator;

    public RegistrationService(RabbitCommandPublisher publisher,
                               RegistrationAggregator aggregator) {
        this.publisher = publisher;
        this.aggregator = aggregator;
    }

    public ApiResponse register(RegisterUserRequest request) {

        String correlationId = UUID.randomUUID().toString();
        UUID userUuid = UUID.randomUUID();

        var future = aggregator.create(correlationId);

        publisher.sendAuth(
                new AuthenticationRegisterCommand(
                        request.getEmail(),
                        request.getPassword(),
                        userUuid
                ),
                correlationId
        );

        publisher.sendUser(
                new UserProfileCommand(
                        request.getUsername(),
                        userUuid
                ),
                correlationId
        );

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            return new ApiResponse(false, "Timeout or error during registration");
        }
    }
}