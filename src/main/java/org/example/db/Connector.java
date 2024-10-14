package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {
    private static final String H2_PATH = "storage/h2/project_expenses_db";
    public Connection connection;

    public Connector() {
        this(H2_PATH);
    }

    public Connector(String path) {
        createConnection(path);
    }

    private void createConnection(String path) {
        String url = "jdbc:h2:".concat(path);
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connection to db established!");
        } catch (SQLException e) {
            System.out.println("Cannot create db-connection!" + e.getMessage());
        }
    }

    public void closeConnection() {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error occurred closing the connection!" + e.getMessage());
            }
        }
    }
}
