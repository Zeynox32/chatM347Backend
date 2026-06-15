package ch.chattrix.authenticationservice.config;

import ch.chattrix.authenticationservice.utils.JwtGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtGenerator jwtGenerator(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration
    ) {
        return new JwtGenerator(secret, accessExpiration, refreshExpiration);
    }
}