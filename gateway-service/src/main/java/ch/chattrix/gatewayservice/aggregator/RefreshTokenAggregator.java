package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.rabbitmq.event.RefreshTokenResultEvent;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.RefreshTokenData;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RefreshTokenAggregator {
    private final Map<String, CompletableFuture<ApiResponse<RefreshTokenData>>> futures =
            new ConcurrentHashMap<>();

    public CompletableFuture<ApiResponse<RefreshTokenData>> createAccessToken(String correlationId) {
        CompletableFuture<ApiResponse<RefreshTokenData>> future = new CompletableFuture<>();
        futures.put(correlationId, future);
        return future;
    }

    public void completeRefreshToken(String correlationId, RefreshTokenResultEvent event) {

        CompletableFuture<ApiResponse<RefreshTokenData>> future =
                futures.remove(correlationId);

        if (future == null) return;

        ApiResponse<RefreshTokenData> response = new ApiResponse<>();
        response.setSuccess(event.isSuccess());
        response.setMessage(event.isSuccess() ? "REFRESH_SUCCESS" : event.getErrorMessage());
        RefreshTokenData refreshTokenData = new RefreshTokenData();
        refreshTokenData.setAccessToken(event.getAccessToken());
        response.setData(refreshTokenData);

        future.complete(response);
    }
}
