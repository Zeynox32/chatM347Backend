package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.event.GetAllUsersResultEvent;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.UserAnonymData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GetAllUsersAggregator {

    private final Map<String, CompletableFuture<ApiResponse<List<UserAnonymData>>>> futures =
            new ConcurrentHashMap<>();

    public CompletableFuture<ApiResponse<List<UserAnonymData>>> getAllUsers(String correlationId) {
        CompletableFuture<ApiResponse<List<UserAnonymData>>> future = new CompletableFuture<>();
        futures.put(correlationId, future);
        return future;
    }

    public void completeGetAllUsers(String correlationId, GetAllUsersResultEvent event) {

        CompletableFuture<ApiResponse<List<UserAnonymData>>> future =
                futures.remove(correlationId);

        if (future == null) return;

        ApiResponse<List<UserAnonymData>> response = new ApiResponse<>();

        response.setSuccess(event.isSuccess());
        response.setMessage(
                event.isSuccess()
                        ? "GET_ALL_USERS_SUCCESS"
                        : event.getErrorMessage()
        );

        response.setData(event.getUsers());

        future.complete(response);
    }
}