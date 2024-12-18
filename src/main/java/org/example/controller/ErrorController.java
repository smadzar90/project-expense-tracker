package org.example.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.controller.base.BaseResponseHandler;

public class ErrorController extends BaseResponseHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        handleNotFound(exchange);
    }
}
