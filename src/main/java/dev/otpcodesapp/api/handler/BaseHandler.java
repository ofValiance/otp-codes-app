package dev.otpcodesapp.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.otpcodesapp.api.exception.*;
import dev.otpcodesapp.util.JsonUtil;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public abstract class BaseHandler extends JsonUtil implements HttpHandler {

    protected final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            dispatch(exchange);
        } catch (UsernameAlreadyTakenException | AdminAlreadyExistsException e) {
            sendError(exchange, 409, "Conflict: " + e.getMessage());
        } catch (InvalidCredentialsException e) {
            sendError(exchange, 401, "Unauthorized: " + e.getMessage());
        } catch (CodeDoesNotExistException | ExpiredCodeException | InvalidCodeException | CanNotDeleteAdminException e) {
            sendError(exchange, 400, "Bad Request: " + e.getMessage());
        } catch (ConfigNotFoundException | UserNotFoundException e) {
            sendError(exchange, 404, "No content: " + e.getMessage());
        } catch (Exception e) {
            sendError(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    protected abstract void dispatch(HttpExchange exchange) throws Exception;

    protected <T> T parseBody(HttpExchange exchange, Class<T> object) throws IOException {
        String jsonString = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return mapper.readValue(jsonString, object);
    }
}
