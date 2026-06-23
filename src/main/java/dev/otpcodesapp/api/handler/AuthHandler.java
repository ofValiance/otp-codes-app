package dev.otpcodesapp.api.handler;

import com.sun.net.httpserver.HttpExchange;

import dev.otpcodesapp.api.dto.request.LoginRequest;
import dev.otpcodesapp.api.dto.response.LoginResponse;
import dev.otpcodesapp.api.dto.request.RegisterRequest;
import dev.otpcodesapp.service.AuthService;


public class AuthHandler extends BaseHandler {

    private final AuthService authService;

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void dispatch(HttpExchange exchange) throws Exception {

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (method.equals("POST")) {
            switch (path) {
                case "/api/auth/register":
                    authService.register(parseBody(exchange, RegisterRequest.class));
                    sendSuccess(exchange, 201, "User registered successfully");
                    break;
                case "/api/auth/login":
                    LoginResponse response = authService.login(parseBody(exchange, LoginRequest.class));
                    sendJson(exchange, 201, response);
                    break;
                default:
                    sendError(exchange, 404, "Not Found");
                    break;
            }
        }
    }
}
