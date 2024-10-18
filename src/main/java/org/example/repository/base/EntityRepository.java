package org.example.repository.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public abstract class EntityRepository<T> {
    private final Connection connection;

    protected EntityRepository(Connection connection) {
        this.connection = connection;
    }

    public Optional<T> findByID(long id) {
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
    protected abstract T mapResultSetToEntity(ResultSet rs);
}
