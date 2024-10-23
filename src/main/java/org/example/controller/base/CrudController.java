package org.example.controller.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class CrudController<T> extends BaseResponseHandler implements HttpHandler {
    private final String ERR_MESSAGE_500 = "Internal Server Error. Please try again later.";

    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        String relativePath = path.substring(getPath().length());

        if(relativePath.equals("/save") && method.equals("POST")) {
            handleSave(exchange);
        } else if(relativePath.equals("/save/all") && method.equals("POST")) {
            handleSaveAll();
        } else if(relativePath.equals("/all") && method.equals("GET")) {
            handleFindAll();
        } else if(relativePath.matches("/\\d+") && method.equals("GET")) {
            handleFindByID();
        } else {
            handleNotFound(exchange);
        }

        exchange.close();
    }

    private void handleSave(HttpExchange exchange) {
        try {
            String json = convertInputStreamToString(exchange.getRequestBody());
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            T entity = parseJsonToEntity(jsonObject);
            saveEntity(entity);
        } catch (RuntimeException | IOException e) {
            System.err.println(e.getMessage());
            sendErrorResponse(exchange, ERR_MESSAGE_500, 500);
        }
    }

    private void handleFindByID() {
    }

    private void handleFindAll() {
    }

    private void handleSaveAll() {
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        bufferedReader.close();
        return sb.toString();
    }

    protected abstract String getPath();
    protected abstract String getName();
    protected abstract T parseJsonToEntity(JsonObject jsonObject);
    protected abstract void saveEntity(T entity);
}
