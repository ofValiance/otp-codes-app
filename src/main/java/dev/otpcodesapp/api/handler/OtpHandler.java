package dev.otpcodesapp.api.handler;

import com.sun.net.httpserver.HttpExchange;

import dev.otpcodesapp.api.dto.AuthorizedUser;
import dev.otpcodesapp.api.dto.request.GenerateRequest;
import dev.otpcodesapp.api.dto.request.ValidateRequest;
import dev.otpcodesapp.service.OtpService;


public class OtpHandler extends BaseHandler {

    private final OtpService otpService;

    public OtpHandler(OtpService otpService) {
        this.otpService = otpService;
    }

    @Override
    protected void dispatch(HttpExchange exchange) throws Exception {

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        AuthorizedUser user = (AuthorizedUser) exchange.getAttribute("authorizedUser");

        if (method.equals("POST")) {
            switch (path) {
                case "/api/otp/generate":
                    otpService.generate(user.id(), parseBody(exchange, GenerateRequest.class));
                    sendSuccess(exchange, 201, "Code has been generated");
                    break;
                case "/api/otp/validate":
                    otpService.validate(user.id(), parseBody(exchange, ValidateRequest.class));
                    sendSuccess(exchange, 201, "Code has been validated");
                    break;
                default:
                    sendError(exchange, 404, "Not Found");
                    break;
            }
        }
    }
}
