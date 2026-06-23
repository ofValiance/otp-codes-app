package dev.otpcodesapp.api.middleware;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import dev.otpcodesapp.api.dto.AuthorizedUser;
import dev.otpcodesapp.util.JsonUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JwtAuthFilter extends Filter {

    private final JwtUtil jwtUtil;
    protected final ObjectMapper mapper = new ObjectMapper();

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
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
                    claims.getSubject(),
                    claims.get("role", String.class)
            );
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
