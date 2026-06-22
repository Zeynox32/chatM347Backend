package ch.chattrix.chatservice.rabbitmq;

import ch.chattrix.shared.rabbitmq.command.UserUsernamesGetCommand;
import ch.chattrix.shared.rabbitmq.event.GetUsernamesResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class UsernameClient {

    private final RabbitCommandPublisher publisher;
    private final GetUsernamesAggregator aggregator;

    public CompletableFuture<Map<UUID, String>> getUsernames(List<UUID> userUuids) {

        String correlationId = UUID.randomUUID().toString();

        CompletableFuture<GetUsernamesResultEvent> future =
                aggregator.create(correlationId);

        publisher.sendGetUsernamesRequest(
                new UserUsernamesGetCommand(userUuids),
                correlationId
        );

        return future.thenApply(result -> {

            if (!result.isSuccess() || result.getUsernames() == null) {
                return Map.of();
            }

            return result.getUsernames();
        });
    }
}