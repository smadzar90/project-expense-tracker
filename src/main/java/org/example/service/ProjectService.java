package org.example.service;

import java.sql.Connection;

public class ProjectService {
    private Connection connection;

    public ProjectService(Connection connection) {
        this.connection = connection;
    }
}
