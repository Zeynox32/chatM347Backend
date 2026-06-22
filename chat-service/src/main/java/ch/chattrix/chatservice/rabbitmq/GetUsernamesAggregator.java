package ch.chattrix.chatservice.rabbitmq;

import ch.chattrix.shared.rabbitmq.event.GetUsernamesResultEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Profile("!test")
@Component
public class GetUsernamesAggregator {

    private final Map<String, CompletableFuture<GetUsernamesResultEvent>> futures =
            new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private static final long TIMEOUT_SECONDS = 5;

    public CompletableFuture<GetUsernamesResultEvent> create(String correlationId) {

        CompletableFuture<GetUsernamesResultEvent> future =
                new CompletableFuture<>();

        futures.put(correlationId, future);

        scheduler.schedule(
                () -> fail(correlationId),
                TIMEOUT_SECONDS,
                TimeUnit.SECONDS
        );

        return future;
    }

    public void complete(String correlationId, GetUsernamesResultEvent event) {

        CompletableFuture<GetUsernamesResultEvent> future =
                futures.remove(correlationId);

        if (future != null) {
            future.complete(event);
        }
    }

    private void fail(String correlationId) {

        CompletableFuture<GetUsernamesResultEvent> future =
                futures.remove(correlationId);

        if (future != null) {

            GetUsernamesResultEvent event =
                    new GetUsernamesResultEvent();

            event.setSuccess(false);
            event.setErrorMessage("TIMEOUT");

            future.complete(event);
        }
    }
}