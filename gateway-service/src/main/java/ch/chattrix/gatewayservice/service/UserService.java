package ch.chattrix.gatewayservice.service;

import ch.chattrix.gatewayservice.aggregator.*;
import ch.chattrix.gatewayservice.rabbitmq.RabbitCommandPublisher;
import ch.chattrix.shared.command.EmptyBasicCommand;
import ch.chattrix.shared.command.UserEditCredentialCommand;
import ch.chattrix.shared.command.UserEditUsernameCommand;
import ch.chattrix.shared.command.UserUuidBasicCommand;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.UserAnonymData;
import ch.chattrix.shared.types.UserData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private final RabbitCommandPublisher publisher;
    private final GetAllUsersAggregator getAllUsersAggregator;
    private final GetOneUserAggregator getOneUserAggregator;
    private final EditUsernameAggregator editUsernameAggregator;
    private final DeleteUserAggregator deleteUserAggregator;

    public UserService(
            RabbitCommandPublisher publisher,
            GetAllUsersAggregator getAllUsersAggregator,
            GetOneUserAggregator getOneUserAggregator,
            EditUsernameAggregator editUsernameAggregator, DeleteUserAggregator deleteUserAggregator) {
        this.publisher = publisher;
        this.getAllUsersAggregator = getAllUsersAggregator;
        this.getOneUserAggregator = getOneUserAggregator;
        this.editUsernameAggregator = editUsernameAggregator;
        this.deleteUserAggregator = deleteUserAggregator;
    }

    public ApiResponse<List<UserAnonymData>> getAllUsers() {

        String correlationId = UUID.randomUUID().toString();

        CompletableFuture<ApiResponse<List<UserAnonymData>>> future =
                getAllUsersAggregator.getAllUsers(correlationId);

        publisher.sendGetAllUsersRequest(
                new EmptyBasicCommand(),
                correlationId
        );

        try {
            return future.get(5, TimeUnit.SECONDS);

        } catch (Exception e) {

            ApiResponse<List<UserAnonymData>> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("TIMEOUT_OR_ERROR");
            response.setData(null);

            return response;
        }
    }

    public ApiResponse<UserData> getOneUser(UUID userUuid) {

        String correlationId = UUID.randomUUID().toString();

        CompletableFuture<ApiResponse<UserData>> future =
                getOneUserAggregator.getOneUser(correlationId);

        publisher.sendGetOneUserBaseRequest(
                new UserUuidBasicCommand(userUuid),
                correlationId
        );

        publisher.sendGetOneUserEmailRequest(
                new UserUuidBasicCommand(userUuid),
                correlationId
        );

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {

            future.cancel(true);

            ApiResponse<UserData> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("TIMEOUT_OR_ERROR");
            response.setData(null);
            return response;
        }
    }

    public ApiResponse<Void> editUsername(String username, UUID userUuid) {

        String correlationId = UUID.randomUUID().toString();

        CompletableFuture<ApiResponse<Void>> future =
                editUsernameAggregator.editUsername(correlationId);

        publisher.sendEditUsernameRequest(
                new UserEditUsernameCommand(userUuid, username),
                correlationId
        );

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {

            future.cancel(true);

            ApiResponse<Void> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("TIMEOUT_OR_ERROR");
            response.setData(null);
            return response;
        }
    }

    public ApiResponse<Void> deleteUser(UUID userUuid) {

        String correlationId = UUID.randomUUID().toString();

        CompletableFuture<ApiResponse<Void>> future =
                deleteUserAggregator.createDelete(correlationId);

        publisher.sendAuthDeletionRequest(
                new UserUuidBasicCommand(userUuid),
                correlationId
        );

        publisher.sendUserDeletionRequest(
                new UserUuidBasicCommand(userUuid),
                correlationId
        );

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {

            future.cancel(true);

            ApiResponse<Void> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("TIMEOUT_OR_ERROR");
            response.setData(null);
            return response;
        }
    }
}