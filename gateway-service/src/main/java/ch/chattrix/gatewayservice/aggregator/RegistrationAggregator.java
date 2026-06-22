package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.rabbitmq.event.BasicRabbitMqResultEvent;
import ch.chattrix.shared.response.ApiResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class RegistrationAggregator {

    private final Map<String, AuthAndUserServiceState> store = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<ApiResponse<Void>>> futures = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final long TIMEOUT_SECONDS = 5;

    public CompletableFuture<ApiResponse<Void>> createRegistration(String correlationId) {

        AuthAndUserServiceState state = new AuthAndUserServiceState();
        store.put(correlationId, state);

        CompletableFuture<ApiResponse<Void>> future = new CompletableFuture<>();
        futures.put(correlationId, future);

        scheduler.schedule(() -> timeout(correlationId), TIMEOUT_SECONDS, TimeUnit.SECONDS);

        return future;
    }

    public void handleAuth(String correlationId, BasicRabbitMqResultEvent event) {
        if (correlationId == null || event == null) return;

        AuthAndUserServiceState state = store.get(correlationId);
        if (state == null) return;

        state.setAuth(event);

        tryComplete(correlationId, state);
    }

    public void handleUser(String correlationId, BasicRabbitMqResultEvent event) {
        if (correlationId == null || event == null) return;

        AuthAndUserServiceState state = store.get(correlationId);
        if (state == null) return;

        state.setUser(event);

        tryComplete(correlationId, state);
    }

    private void tryComplete(String correlationId, AuthAndUserServiceState state) {

        if (state.getAuth() == null || state.getUser() == null) {
            return;
        }

        CompletableFuture<ApiResponse<Void>> future = futures.get(correlationId);
        if (future == null || future.isDone()) return;

        boolean success = state.getAuth().isSuccess()
                && state.getUser().isSuccess();

        String message;

        if (!state.getAuth().isSuccess()) {
            message = state.getAuth().getErrorMessage();
        } else if (!state.getUser().isSuccess()) {
            message = state.getUser().getErrorMessage();
        } else {
            message = "USER_REGISTERED_SUCCESSFULLY";
        }

        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(success);
        response.setMessage(message);
        response.setData(null);

        completeSafely(correlationId, future, response);
    }

    private void timeout(String correlationId) {

        CompletableFuture<ApiResponse<Void>> future = futures.get(correlationId);
        AuthAndUserServiceState state = store.get(correlationId);

        if (future == null || future.isDone()) return;

        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage("TIMEOUT");
        response.setData(null);

        completeSafely(correlationId, future, response);
    }

    private void completeSafely(
            String correlationId,
            CompletableFuture<ApiResponse<Void>> future,
            ApiResponse<Void> response
    ) {
        if (future != null && !future.isDone()) {
            future.complete(response);
        }

        cleanup(correlationId);
    }

    private void cleanup(String correlationId) {
        store.remove(correlationId);
        futures.remove(correlationId);
    }
}