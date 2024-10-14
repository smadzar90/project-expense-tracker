package org.example.repository;

import java.net.ConnectException;
import java.sql.Connection;

public abstract class CrudRepository<T> {
    private Connection connection;

    public CrudRepository(Connection connection) {
        this.connection = connection;
    }


}
