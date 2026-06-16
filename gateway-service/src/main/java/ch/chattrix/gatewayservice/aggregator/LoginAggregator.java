package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.event.BasicRabbitMqResultEvent;
import ch.chattrix.shared.event.LoginResultEvent;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.LoginData;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAggregator {

    private final Map<String, CompletableFuture<ApiResponse<LoginData>>> futures =
            new ConcurrentHashMap<>();

    public CompletableFuture<ApiResponse<LoginData>> createLogin(String correlationId) {
        CompletableFuture<ApiResponse<LoginData>> future = new CompletableFuture<>();
        futures.put(correlationId, future);
        return future;
    }

    public void completeLogin(String correlationId, LoginResultEvent event) {

        CompletableFuture<ApiResponse<LoginData>> future =
                futures.remove(correlationId);

        if (future == null) return;

        ApiResponse<LoginData> response = new ApiResponse<>();
        response.setSuccess(event.isSuccess());
        response.setMessage(event.isSuccess() ? "LOGIN_SUCCESS" : event.getErrorMessage());
        LoginData loginData = new LoginData();
        loginData.setAccessToken(event.getAccessToken());
        loginData.setRefreshToken(event.getRefreshToken());
        response.setData(loginData);

        future.complete(response);
    }
}