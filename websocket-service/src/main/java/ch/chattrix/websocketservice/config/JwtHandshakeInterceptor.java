package ch.chattrix.websocketservice.config;

import ch.chattrix.shared.utils.JwtValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtValidator jwtValidator;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {

        String cookieHeader = request.getHeaders().getFirst("Cookie");

        if (cookieHeader == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String accessToken = null;

        String[] cookies = cookieHeader.split(";");

        for (String cookie : cookies) {
            String[] parts = cookie.trim().split("=");

            if (parts.length == 2 && parts[0].equals("accessToken")) {
                accessToken = parts[1];
                break;
            }
        }

        if (accessToken == null || !jwtValidator.isTokenValid(accessToken)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        UUID userUuid = UUID.fromString(jwtValidator.extractSubject(accessToken));

        attributes.put("userUuid", userUuid);

        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }
}