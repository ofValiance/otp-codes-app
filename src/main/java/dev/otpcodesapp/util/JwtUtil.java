package dev.otpcodesapp.util;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;


public class JwtUtil {

    private final SecretKey jwtKey;
    private final long ttlMillis;

    public JwtUtil(long ttlMinutes) {
        Dotenv dotenv = Dotenv.load();
        jwtKey = Keys.hmacShaKeyFor(dotenv.get("JWT_KEY").getBytes(StandardCharsets.UTF_8));
        this.ttlMillis = ttlMinutes * 60 * 1000;
    }

    public String generateToken(Long id, String login, String role) {
        return Jwts.builder()
                .subject(id.toString())
                .claim("login", login)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttlMillis))
                .signWith(jwtKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(jwtKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
