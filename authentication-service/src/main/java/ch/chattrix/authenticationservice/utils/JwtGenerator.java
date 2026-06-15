package ch.chattrix.authenticationservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtGenerator {

    private final Key signingKey;
    private final long expirationMillis;

    public JwtGenerator(String secret, long expirationMillis) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String subject) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMillis))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            return extractClaims(token)
                    .getExpiration()
                    .after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}