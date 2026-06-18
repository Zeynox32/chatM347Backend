package ch.chattrix.authenticationservice.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtGenerator {

    private final Key signingKey;
    private final long accessTokenExpirationMillis;

    public JwtGenerator(String secret, long accessTokenExpirationMillis) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
    }

    public String generateAccessToken(String userUuid, String email) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(userUuid)
                .claim("email", email)
                .claim("type", "access")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTokenExpirationMillis))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}