package org.example.controller.base;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;

public class BaseResponseHandler {
    protected static final Gson gson = new Gson();

    protected void handleNotFound(HttpExchange exchange) {
        String message = "Resource not found.";
        sendErrorResponse(exchange, message, 404);
    }

    protected static <T> void sendJsonResponse(HttpExchange exchange, T object, int statusCode)  {
        String json = gson.toJson(object);
        sendResponse(exchange, json, statusCode);
    }

    protected static void sendErrorResponse(HttpExchange exchange, String message, int statusCode) {
        String error = "{"
                + "\"error\": {"
                + "\"code\":\"%d\","
                + "\"message\":\"%s\""
                + "}"
                + "}";
        String json = String.format(error, statusCode, message);
        sendResponse(exchange, json, statusCode);
    }

    private static void sendResponse(HttpExchange exchange, String json, int statusCode) {
        try(OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(statusCode, json.getBytes().length);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            os.write(json.getBytes());
            os.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error sending a response: " + e);
        }
    }
}
