package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.rabbitmq.event.GetOneUserBasicDataResultEvent;
import ch.chattrix.shared.rabbitmq.event.GetOneUserEmailDataResultEvent;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.UserData;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class GetOneUserAggregator {

    private final Map<String, CompletableFuture<ApiResponse<UserData>>> futures =
            new ConcurrentHashMap<>();

    private final Map<String, UserState> store =
            new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private static final long TIMEOUT_SECONDS = 5;

    public CompletableFuture<ApiResponse<UserData>> getOneUser(String correlationId) {
        CompletableFuture<ApiResponse<UserData>> future = new CompletableFuture<>();

        futures.put(correlationId, future);
        store.put(correlationId, new UserState());

        scheduler.schedule(() -> fail(correlationId, "TIMEOUT"), TIMEOUT_SECONDS, TimeUnit.SECONDS);

        return future;
    }

    public void handleUserBaseData(String correlationId,
                                   GetOneUserBasicDataResultEvent event) {

        UserState state = store.get(correlationId);
        if (state == null || !event.isSuccess()) return;

        state.user.setUserUuid(event.getUserUuid());
        state.user.setUsername(event.getUsername());
        state.user.setCreatedAt(event.getCreatedAt());

        state.userReceived = true;

        tryComplete(correlationId, state);
    }

    public void handleEmail(String correlationId,
                            GetOneUserEmailDataResultEvent event) {

        UserState state = store.get(correlationId);
        if (state == null || !event.isSuccess()) return;

        state.user.setEmail(event.getEmail());

        state.authReceived = true;

        tryComplete(correlationId, state);
    }

    private void tryComplete(String correlationId, UserState state) {

        if (state.userReceived && state.authReceived) {

            ApiResponse<UserData> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("GET_ONE_USER_SUCCESS");
            response.setData(state.user);

            complete(correlationId, response);
        }
    }

    private void fail(String correlationId, String message) {

        ApiResponse<UserData> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);

        complete(correlationId, response);
    }

    private void complete(String correlationId,
                          ApiResponse<UserData> response) {

        CompletableFuture<ApiResponse<UserData>> future =
                futures.remove(correlationId);

        store.remove(correlationId);

        if (future != null) {
            future.complete(response);
        }
    }
}