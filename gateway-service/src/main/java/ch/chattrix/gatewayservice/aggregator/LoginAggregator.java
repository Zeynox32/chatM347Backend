package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.event.user.UserLoginResultEvent;
import ch.chattrix.shared.response.LoginApiResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAggregator {

    private final Map<String, CompletableFuture<LoginApiResponse>> futures =
            new ConcurrentHashMap<>();

    public CompletableFuture<LoginApiResponse> createLogin(String correlationId) {

        CompletableFuture<LoginApiResponse> future = new CompletableFuture<>();
        futures.put(correlationId, future);

        return future;
    }

    public void handleLogin(
            String correlationId,
            UserLoginResultEvent event
    ) {

        CompletableFuture<LoginApiResponse> future =
                futures.remove(correlationId);

        if (future == null) {
            return;
        }

        future.complete(
                new LoginApiResponse(
                        event.isSuccess(),
                        null,
                        null, event.isSuccess()
                        ? "Login successful"
                        : event.getErrorMessage()
                )
        );
    }
}