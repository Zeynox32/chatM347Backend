package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.event.BasicRabbitMqResultEvent;
import ch.chattrix.shared.response.ApiResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RegistrationAggregator {

    private final Map<String, RegistrationState> store = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<ApiResponse<Void>>> futures = new ConcurrentHashMap<>();

    public CompletableFuture<ApiResponse<Void>> createRegistration(String correlationId) {
        store.put(correlationId, new RegistrationState());
        CompletableFuture<ApiResponse<Void>> future = new CompletableFuture<>();
        futures.put(correlationId, future);
        return future;
    }

    public void handleAuth(String correlationId, BasicRabbitMqResultEvent event) {

        RegistrationState state = store.get(correlationId);
        if (state == null) return;

        state.setAuth(event);
        tryComplete(correlationId, state);
    }

    public void handleUser(String correlationId, BasicRabbitMqResultEvent event) {

        RegistrationState state = store.get(correlationId);
        if (state == null) return;

        state.setUser(event);
        tryComplete(correlationId, state);
    }

    private void tryComplete(String correlationId, RegistrationState state) {

        if (state.getAuth() == null || state.getUser() == null) {
            return;
        }

        boolean success =
                state.getAuth().isSuccess() &&
                        state.getUser().isSuccess();

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

        CompletableFuture<ApiResponse<Void>> future =
                futures.remove(correlationId);

        if (future != null) {
            future.complete(response);
        }

        store.remove(correlationId);
    }
}