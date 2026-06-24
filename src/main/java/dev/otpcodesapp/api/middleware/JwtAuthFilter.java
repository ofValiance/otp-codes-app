package dev.otpcodesapp.api.middleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import dev.otpcodesapp.api.dto.AuthorizedUser;
import dev.otpcodesapp.model.User;
import dev.otpcodesapp.util.JsonUtil;

import dev.otpcodesapp.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import java.io.IOException;


public class JwtAuthFilter extends Filter {

    private final JwtUtil jwtUtil;
    private final User.Role requiredRole;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(JwtUtil jwtUtil, User.Role requiredRole) {
        this.jwtUtil = jwtUtil;
        this.requiredRole = requiredRole;
        logger.info("JwtAuthFilter initialized for role: {}", requiredRole);
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {

        logger.debug("Filtering request: {} {}", exchange.getRequestMethod(), exchange.getRequestURI());

        String header = exchange.getRequestHeaders().getFirst("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("Missing or malformed authorization header for request: {}", exchange.getRequestURI());
            JsonUtil.sendError(exchange, 401, "Missing authorization header");
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = jwtUtil.parseToken(token);
            AuthorizedUser authorizedUser = new AuthorizedUser(
                    Long.parseLong(claims.getSubject()),
                    claims.get("login", String.class),
                    claims.get("role", String.class)
            );
            if (!requiredRole.equals(User.Role.valueOf(authorizedUser.role()))) {
                logger.warn("Access denied for user '{}' with role '{}'. Required role: '{}'",
                        authorizedUser.login(), authorizedUser.role(), requiredRole);
                JsonUtil.sendError(exchange, 403, "Access denied: insufficient privileges");
                return;
            }
            exchange.setAttribute("authorizedUser", authorizedUser);
            logger.info("User '{}' authorized successfully with role '{}'", authorizedUser.login(), authorizedUser.role());
            chain.doFilter(exchange);
        } catch (ExpiredJwtException e) {
            logger.warn("Expired token provided for request: {}", exchange.getRequestURI());
            JsonUtil.sendError(exchange, 401, "Token expired");
        } catch (JwtException e) {
            logger.warn("Invalid token provided for request: {}", exchange.getRequestURI());
            JsonUtil.sendError(exchange, 401, "Invalid token");
        } catch (Exception e) {
            logger.error("Internal server error during authentication for request: {}", exchange.getRequestURI(), e);
            JsonUtil.sendError(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    public String description() {
        return "JWT Authentication Filter";
    }
}
