package ch.chattrix.websocketservice.config;

import ch.chattrix.shared.utils.JwtValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtValidator jwtValidator(@Value("${jwt.secret}") String secret) {
        return new JwtValidator(secret);
    }
}