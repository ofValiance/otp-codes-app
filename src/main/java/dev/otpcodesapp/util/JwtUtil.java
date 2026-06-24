package dev.otpcodesapp.util;

import dev.otpcodesapp.api.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.otpcodesapp.config.EnvManager;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;


public class JwtUtil {

    private final SecretKey jwtKey;
    private final long ttlMillis;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(long ttlMinutes) {
        logger.info("Initializing JwtUtil with TTL: {} minutes", ttlMinutes);
        jwtKey = Keys.hmacShaKeyFor(EnvManager.get("JWT_KEY").getBytes(StandardCharsets.UTF_8));
        this.ttlMillis = ttlMinutes * 60 * 1000;
    }

    public String generateToken(Long id, String login, String role) {
        logger.debug("Generating JWT token for user id={}, login={}, role={}", id, login, role);
        String token = Jwts.builder()
                .subject(id.toString())
                .claim("login", login)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttlMillis))
                .signWith(jwtKey)
                .compact();
        logger.info("JWT token successfully generated for user id={}", id);
        return token;
    }

    public Claims parseToken(String token) {
        logger.debug("Parsing JWT token");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            logger.debug("JWT token successfully parsed for subject={}", claims.getSubject());
            return claims;
        } catch (Exception e) {
            logger.error("Failed to parse JWT token: {}", e.getMessage());
            throw new BusinessException("Failed to parse JWT token");
        }
    }
}
