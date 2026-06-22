package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.rabbitmq.event.BasicRabbitMqResultEvent;
import ch.chattrix.shared.response.ApiResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EditUsernameAggregator {
    private final Map<String, CompletableFuture<ApiResponse<Void>>> futures =
            new ConcurrentHashMap<>();

    public CompletableFuture<ApiResponse<Void>> editUsername(String correlationId) {
        CompletableFuture<ApiResponse<Void>> future = new CompletableFuture<>();
        futures.put(correlationId, future);
        return future;
    }

    public void completeEditUser(String correlationId, BasicRabbitMqResultEvent event) {

        CompletableFuture<ApiResponse<Void>> future =
                futures.remove(correlationId);

        if (future == null) return;

        ApiResponse<Void> response = new ApiResponse<>();

        response.setSuccess(event.isSuccess());
        response.setMessage(
                event.isSuccess()
                        ? "EDIT_USER_SUCCESS"
                        : event.getErrorMessage()
        );

        response.setData(null);
        future.complete(response);
    }
}
