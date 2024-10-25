package org.example.controller.base;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.List;
import java.util.Optional;

public abstract class EntityController<T> extends BaseResponseHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        String relativePath = path.substring(getPath().length());

        if(relativePath.equals("/all") && method.equals("GET")) {
            handleFindAll(exchange);
        } else if(relativePath.matches("/\\d+") && method.equals("GET")) {
            handleFindByID(exchange, relativePath);
        } else {
            handleNotFound(exchange);
        }

        exchange.close();
    }

    private void handleFindAll(HttpExchange exchange) {
        try {
            List<T> entities = findAll();
            sendJsonResponse(exchange, entities, 200);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            sendErrorResponse(exchange, ERR_MESSAGE_500, 500);
        }
    }

    private void handleFindByID(HttpExchange exchange, String relativePath) {
        try {
            Long id = Long.valueOf(relativePath.substring(1));
            Optional<T> entity = findByID(id);

            if(entity.isPresent()) {
                sendJsonResponse(exchange, entity.get(), 200);
            } else {
                String message = String.format(getName() + " with ID %d not found.", id);
                sendErrorResponse(exchange, message, 404);
            }
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            sendErrorResponse(exchange, ERR_MESSAGE_500, 500);
        }
    }

    protected abstract String getPath();
    protected abstract String getName();
    protected abstract List<T> findAll();
    protected abstract Optional<T> findByID(Long id);
}

