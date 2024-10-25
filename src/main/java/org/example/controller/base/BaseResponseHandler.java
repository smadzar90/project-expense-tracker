package org.example.controller.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import org.example.model.Expense;
import org.example.utils.CustomLocalDateAdapter;
import org.example.utils.CustomLocalDateTimeAdapter;
import org.example.utils.ExpenseDeserializer;
import org.example.utils.ExpenseSerializer;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BaseResponseHandler {
    protected static final Gson gson = createGson();
    protected static final String ERR_MESSAGE_500 = "Internal Server Error. Please try again later.";
    protected static final String ERR_JSON_PARSE_400 = "Couldn't parse JSON to entity.";
    protected static final String ERR_INVALID_FIELD_400 = "Invalid input. Check fields and try again.";
    protected static final String ERR_QUERY_PARAMS_400 = "Invalid query parameters. Please try again.";

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new CustomLocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new CustomLocalDateTimeAdapter())
                .registerTypeAdapter(Expense.class, new ExpenseDeserializer())
                .registerTypeAdapter(Expense.class, new ExpenseSerializer())
                .setPrettyPrinting()
                .create();
    }

    protected static void handleNotFound(HttpExchange exchange) {
        String message = "Resource not found.";
        sendErrorResponse(exchange, message, 404);
    }

    protected static <T> void sendJsonResponse(HttpExchange exchange, T object, int statusCode)  {
        String json = gson.toJson(object);
        sendResponse(exchange, json, statusCode);
    }

    protected static void sendJsonResponse(HttpExchange exchange, String message, int statusCode)  {
        String json = String.format("{\"message\":\"%s\"}", message);
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
            sendErrorResponse(exchange, ERR_MESSAGE_500, 500);
        }
    }
}
