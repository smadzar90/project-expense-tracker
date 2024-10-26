package org.example.controller.base;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

public abstract class CrudController<T> extends BaseResponseHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        String relativePath = path.substring(getPath().length());

        if(relativePath.equals("/save") && method.equals("POST")) {
            handleSave(exchange);
        } else if(relativePath.equals("/save/all") && method.equals("POST")) {
            handleSaveAll(exchange);
        } else if(relativePath.equals("/find/all") && method.equals("GET")) {
            handleFindAll(exchange);
        } else if(relativePath.matches("/find/\\d+") && method.equals("GET")) {
            handleFindByID(exchange, relativePath);
        } else if(relativePath.equals("/update") && method.equals("PUT")) {
            handleUpdate(exchange);
        } else if(relativePath.equals("/update/all") && method.equals("PUT")) {
            handleUpdateAll(exchange);
        } else if(relativePath.matches("/delete/\\d+") && method.equals("DELETE")) {
            handleDelete(exchange, relativePath);
        } else if(relativePath.equals("/delete/all") && method.equals("DELETE")) {
            handleDeleteAll(exchange);
        } else {
            handleNotFound(exchange);
        }

        exchange.close();
    }

    private void handleSave(HttpExchange exchange) {
        try {
            JsonObject jsonObject = getJsonObject(exchange);

            T entity = parseJsonToEntity(jsonObject);
            T savedEntity = saveEntity(entity);
            sendJsonResponse(exchange, savedEntity, 201);

        } catch (Exception e) {
            handleException(e, exchange);
        }
    }

    private void handleSaveAll(HttpExchange exchange) {
        try {
            JsonArray jsonArray = getJsonArray(exchange);
            Set<T> entities = convertJsonArrayToSet(jsonArray);

            List<T> savedEntities = saveAllEntities(entities);
            sendJsonResponse(exchange, savedEntities, 201);

        } catch (Exception e) {
            handleException(e, exchange);
        }
    }

    private void handleFindAll(HttpExchange exchange) {
        try {
            String query = exchange.getRequestURI().getQuery();

            if(query == null) {
                List<T> entities = findAllEntities();

                sendJsonResponse(exchange, entities, 200);
            } else {
                handleFindAllByAttribute(exchange, queryToMap(query));
            }
        } catch (Exception e) {
            handleException(e, exchange);
        }
    }

    private void handleFindByID(HttpExchange exchange, String relativePath) {
        try {
            Long id = Long.valueOf(relativePath.substring(6));
            Optional<T> entity = findEntityById(id);

            if(entity.isPresent()) {
                sendJsonResponse(exchange, entity.get(), 200);
            } else {
                String message = String.format(getName() + " with ID %d not found.", id);
                sendErrorResponse(exchange, message, 404);
            }
        } catch (Exception e) {
            handleException(e, exchange);
        }
    }

    private void handleUpdate(HttpExchange exchange) {
        try {
            JsonObject jsonObject = getJsonObject(exchange);

            T entity = parseJsonToEntity(jsonObject);
            updateEntity(entity);

            String message = getName() + " updated successfully.";
            sendJsonResponse(exchange, message, 200);
        } catch (Exception e) {
            handleException(e, exchange);
        }
    }

    private void handleUpdateAll(HttpExchange exchange) {
        try {
            JsonArray jsonArray = getJsonArray(exchange);

            Set<T> entities = convertJsonArrayToSet(jsonArray);
            updateAllEntities(entities);

            String message = "All " + getName() + "s updated successfully.";
            sendJsonResponse(exchange, message, 200);
        } catch (Exception e) {
            handleException(e, exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String relativePath) {
        try {
            Long id = Long.valueOf(relativePath.substring(8));
            deleteEntity(id);

            String message = getName() + " deleted successfully.";
            sendJsonResponse(exchange, message, 200);
        } catch (Exception e) {
            handleException(e, exchange);
        }
    }

    private void handleDeleteAll(HttpExchange exchange) {
        try {
            JsonArray jsonArray = getJsonArray(exchange);
            Set<Long> ids = convertJsonArrayToSetOfLongIds(jsonArray);

            deleteAllEntities(ids);

            String message = "All " + getName() + "s deleted successfully.";
            sendJsonResponse(exchange, message, 200);
        } catch (Exception e) {
            handleException(e, exchange);
        }
    }

    protected void handleFindAllByAttribute(HttpExchange exchange, Map<String, String> queryMap, String attribute) {
        try {
            if(queryMap.containsKey("value")) {
                String value = queryMap.get("value");

                List<T> entities = getAllEntitiesByAttribute(attribute, value);
                sendJsonResponse(exchange, entities, 200);
            } else {
                sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
            }
        } catch (Exception e) {
            handleException(e, exchange);
        }
    }

    protected void handleFindAllByAttributeGreaterOrLessThan(HttpExchange exchange, Map<String, String> queryMap) {
        try {
            if(queryMap.containsKey("greater_than")) {
                BigDecimal value = new BigDecimal(queryMap.get("greater_than"));
                List<T> entities = getAllEntitiesGreaterThan(value);
                sendJsonResponse(exchange, entities, 200);

            } else if(queryMap.containsKey("less_than")) {
                BigDecimal value = new BigDecimal(queryMap.get("less_than"));
                List<T> entities = getAllEntitiesLessThan(value);
                sendJsonResponse(exchange, entities, 200);

            } else {
                sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
            }
        } catch (Exception e) {
            handleException(e, exchange);
        }
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

    private JsonObject getJsonObject(HttpExchange exchange) throws IOException {
        String json = convertInputStreamToString(exchange.getRequestBody());
        return gson.fromJson(json, JsonObject.class);
    }

    private JsonArray getJsonArray(HttpExchange exchange) throws IOException {
        String json = convertInputStreamToString(exchange.getRequestBody());
        return gson.fromJson(json, JsonArray.class);
    }

    private Set<T> convertJsonArrayToSet(JsonArray jsonArray) {
        Set<T> entities = new HashSet<>();
        jsonArray.forEach(e -> entities.add(parseJsonToEntity(e.getAsJsonObject())));
        return entities;
    }

    private Set<Long> convertJsonArrayToSetOfLongIds(JsonArray jsonArray) {
        Set<Long> entities = new HashSet<>();
        jsonArray.forEach(e -> entities.add(e.getAsLong()));
        return entities;
    }

    private void handleException(Exception e, HttpExchange exchange) {
        if (e instanceof JsonParseException) {
            System.err.println(e.getMessage());
            sendErrorResponse(exchange, ERR_JSON_PARSE_400, 400);
        } else if (e instanceof IOException) {
            System.err.println(e.getMessage());
            sendErrorResponse(exchange, ERR_MESSAGE_500, 500);
        } else if (e instanceof RuntimeException) {
            System.err.println(e.getMessage());
            sendErrorResponse(exchange, ERR_INVALID_FIELD_400, 400);
        } else {
            System.err.println("Unexpected error: " + e.getMessage());
            sendErrorResponse(exchange, "Unexpected error.", 500);
        }
    }

    public Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();

        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else{
                result.put(entry[0], "");
            }
        }
        return result;
    }

    protected abstract String getPath();
    protected abstract String getName();
    protected abstract T parseJsonToEntity(JsonObject jsonObject);
    protected abstract T saveEntity(T entity);
    protected abstract List<T> saveAllEntities(Set<T> entities);
    protected abstract List<T> findAllEntities();
    protected abstract Optional<T> findEntityById(Long id);
    protected abstract void updateEntity(T entity);
    protected abstract void updateAllEntities(Set<T> entities);
    protected abstract void deleteEntity(Long id);
    protected abstract void deleteAllEntities(Set<Long> ids);
    protected abstract void handleFindAllByAttribute(HttpExchange exchange, Map<String, String> queryMap);
    protected abstract List<T> getAllEntitiesByAttribute(String attribute, String value);
    protected abstract List<T> getAllEntitiesGreaterThan(BigDecimal value);
    protected abstract List<T> getAllEntitiesLessThan(BigDecimal value);

}
