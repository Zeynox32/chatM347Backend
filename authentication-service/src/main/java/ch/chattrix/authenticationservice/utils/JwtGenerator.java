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
    private final long refreshTokenExpirationMillis;

    public JwtGenerator(String secret, long accessTokenExpirationMillis, long refreshTokenExpirationMillis) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
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

    public String generateRefreshToken(String userUuid) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(userUuid)
                .claim("type", "refresh")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTokenExpirationMillis))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}