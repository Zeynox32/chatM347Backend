package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.event.user.AuthenticationRegisterResultEvent;
import ch.chattrix.shared.event.user.UserProfileResultEvent;
import ch.chattrix.shared.response.BasicApiResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RegistrationAggregator {

    private final Map<String, RegistrationResult> store = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<BasicApiResponse>> futures = new ConcurrentHashMap<>();

    public CompletableFuture<BasicApiResponse> createRegistration(String correlationId) {
        store.put(correlationId, new RegistrationResult());
        CompletableFuture<BasicApiResponse> future = new CompletableFuture<>();
        futures.put(correlationId, future);
        return future;
    }

    public void handleAuth(String correlationId, AuthenticationRegisterResultEvent event) {

        RegistrationResult result = store.get(correlationId);
        if (result == null) return;

        result.setAuthentication(event);
        checkDone(correlationId, result);
    }

    public void handleUser(String correlationId, UserProfileResultEvent event) {

        RegistrationResult result = store.get(correlationId);
        if (result == null) return;

        result.setUser(event);
        checkDone(correlationId, result);
    }

    private void checkDone(String correlationId, RegistrationResult result) {

        if (result.getAuthentication() == null || result.getUser() == null) {
            return;
        }

        boolean success =
                result.getAuthentication().isSuccess() &&
                        result.getUser().isSuccess();

        String message;

        if (!result.getAuthentication().isSuccess()) {
            message = result.getAuthentication().getErrorMessage();
        } else if (!result.getUser().isSuccess()) {
            message = result.getUser().getErrorMessage();
        } else {
            message = "User registered successfully";
        }

        futures.remove(correlationId)
                .complete(new BasicApiResponse(success, message));

        store.remove(correlationId);
    }
}