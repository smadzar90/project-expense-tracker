package org.example.service;

import java.sql.Connection;

public class ExpenseService {
    private Connection connection;

    public ExpenseService(Connection connection) {
        this.connection = connection;
    }
}
