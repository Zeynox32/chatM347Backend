package ch.chattrix.gatewayservice.service;

import ch.chattrix.gatewayservice.aggregator.LoginAggregator;
import ch.chattrix.gatewayservice.aggregator.LogoutAggregator;
import ch.chattrix.gatewayservice.aggregator.RegistrationAggregator;
import ch.chattrix.gatewayservice.rabbitmq.RabbitCommandPublisher;
import ch.chattrix.shared.command.UserCredentialRegisterCommand;
import ch.chattrix.shared.command.UserLoginCommand;
import ch.chattrix.shared.command.UserLogoutCommand;
import ch.chattrix.shared.command.UserRegisterCommand;
import ch.chattrix.shared.dto.LoginUserRequest;
import ch.chattrix.shared.dto.RegisterUserRequest;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.LoginData;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class AuthenticationService {

    private final RabbitCommandPublisher publisher;
    private final RegistrationAggregator registrationAggregator;
    private final LoginAggregator loginAggregator;
    private final LogoutAggregator logoutAggregator;

    public AuthenticationService(
            RabbitCommandPublisher publisher,
            RegistrationAggregator registrationAggregator,
            LoginAggregator loginAggregator,
            LogoutAggregator logoutAggregator
    ) {
        this.publisher = publisher;
        this.registrationAggregator = registrationAggregator;
        this.loginAggregator = loginAggregator;
        this.logoutAggregator = logoutAggregator;
    }

    public ApiResponse<Void> register(RegisterUserRequest request) {

        String correlationId = UUID.randomUUID().toString();
        UUID userUuid = UUID.randomUUID();

        var future = registrationAggregator.createRegistration(correlationId);

        publisher.sendRegisterRequest(
                new UserCredentialRegisterCommand(
                        request.getEmail(),
                        request.getPassword(),
                        userUuid
                ),
                correlationId
        );

        publisher.sendCreateUserRequest(
                new UserRegisterCommand(
                        request.getUsername(),
                        userUuid
                ),
                correlationId
        );

        return getVoidApiResponse(future);
    }

    private ApiResponse<Void> getVoidApiResponse(CompletableFuture<ApiResponse<Void>> future) {
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            ApiResponse<Void> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("UNKNOWN_ERROR");
            response.setData(null);
            return response;
        }
    }

    public ApiResponse<LoginData> login(LoginUserRequest request) {

        String correlationId = UUID.randomUUID().toString();

        var future = loginAggregator.createLogin(correlationId);

        publisher.sendLoginRequest(
                new UserLoginCommand(
                        request.getEmail(),
                        request.getPassword()
                ),
                correlationId
        );
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            ApiResponse<LoginData> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("TIMEOUT_OR_ERROR");
            response.setData(null);
            return response;
        }
    }

    public ApiResponse<Void> logout(UUID userUuid) {

        String correlationId = UUID.randomUUID().toString();

        var future = logoutAggregator.createLogout(correlationId);

        publisher.sendLogoutRequest(
                new UserLogoutCommand(
                        userUuid
                ),
                correlationId
        );

        return getVoidApiResponse(future);
    }
}