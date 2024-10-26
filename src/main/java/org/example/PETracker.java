package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.controller.*;
import org.example.db.Connector;
import org.example.service.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PETracker extends Connector {
    private final ProjectService projectService;
    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final PaymentService paymentService;

    public PETracker() {
        super();
        projectService = new ProjectService(this.connection);
        expenseService = new ExpenseService(this.connection);
        categoryService = new CategoryService(this.connection);
        paymentService = new PaymentService(this.connection);
    }

    private static void startServer() throws IOException, InterruptedException {
        PETracker tracker = new PETracker();
        closeDBConnectionOnJvmShutdown(tracker);

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/api/category", new CategoryController(tracker.categoryService));
        server.createContext("/api/payment/method", new PaymentController(tracker.paymentService));
        server.createContext("/api/expense", new ExpenseController(tracker.expenseService));
        server.createContext("/api/project", new ProjectController(tracker.projectService));
        server.createContext("/", new ErrorController());

        server.start();
        System.out.println("Server started on port 8000. Listening for requests...");
        Thread.currentThread().join();
    }

    private static void closeDBConnectionOnJvmShutdown(PETracker tracker) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                tracker.close();
                System.out.println("\n2Connection to db closed.");
                System.out.println("Server closed on port 8000.");
            } catch (Exception e) {
                System.err.println("Error closing a db connection: " + e.getMessage());
            }
        }));
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        startServer();
    }
}
