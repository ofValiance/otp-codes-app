package dev.otpcodesapp.api.middleware;

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

    public JwtAuthFilter(JwtUtil jwtUtil, User.Role requiredRole) {
        this.jwtUtil = jwtUtil;
        this.requiredRole = requiredRole;
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {

        String header = exchange.getRequestHeaders().getFirst("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
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
                JsonUtil.sendError(exchange, 403, "Access denied: insufficient privileges");
                return;
            }
            exchange.setAttribute("authorizedUser", authorizedUser);
            chain.doFilter(exchange);
        } catch (ExpiredJwtException e) {
            JsonUtil.sendError(exchange, 401, "Token expired");
        } catch (JwtException e) {
            JsonUtil.sendError(exchange, 401, "Invalid token");
        } catch (Exception e) {
            JsonUtil.sendError(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    public String description() {
        return "JWT Authentication Filter";
    }
}
