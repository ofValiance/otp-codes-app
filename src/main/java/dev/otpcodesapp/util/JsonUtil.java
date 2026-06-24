package dev.otpcodesapp.util;

import com.sun.net.httpserver.HttpExchange;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void sendJson(HttpExchange exchange, int status, Object responseObject) throws IOException {
        String jsonString = mapper.writeValueAsString(responseObject);
        byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void sendError(HttpExchange exchange, int status, String message) throws IOException {
        sendJson(exchange, status, Map.of("error", message, "status", status));
    }

    public static void sendSuccess(HttpExchange exchange, int status, String message) throws IOException {
        sendJson(exchange, status, Map.of("success", message, "status", status));
    }
}
