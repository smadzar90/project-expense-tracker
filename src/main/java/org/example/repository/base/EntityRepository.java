package org.example.repository.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class EntityRepository<T> {
    private final Connection connection;

    protected EntityRepository(Connection connection) {
        this.connection = connection;
    }

    public List<T> findAll() {
        try {
            PreparedStatement pStatement = connection.prepareStatement(getFindAllSQL());
            ResultSet rs = pStatement.executeQuery();
            List<T> entities = new ArrayList<>();
            while(rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }
            return entities;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Error occurred while executing a query. " + e.getMessage());
        }
    }

    public Optional<T> findByID(Long id) {
        try {
            PreparedStatement pStatement = connection.prepareStatement(getFindSQL());
            pStatement.setLong(1, id);
            ResultSet rs = pStatement.executeQuery();
            if(rs.next()) {
                return Optional.ofNullable(mapResultSetToEntity(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while executing a query. " + e.getMessage());
        }
    }

    protected abstract String getFindSQL();
    protected abstract String getFindAllSQL();
    protected abstract T mapResultSetToEntity(ResultSet rs);
}
