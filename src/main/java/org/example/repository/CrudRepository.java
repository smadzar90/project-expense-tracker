package org.example.repository;

import org.example.annotations.Id;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class CrudRepository<T> {
    private final Connection connection;

    public CrudRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Optional<T>> saveAll(Set<T> entities) {
        return entities.stream().map(this::save).collect(Collectors.toList());
    }

    public Optional<T> save(T entity) {
        T entityToReturn;
        if(!isIdPresent(entity)) {
            try {
                PreparedStatement pStatement = connection.prepareStatement(getSaveSQL(), PreparedStatement.RETURN_GENERATED_KEYS);
                mapEntityToStatement(pStatement, entity);
                pStatement.executeUpdate();
                ResultSet rs = pStatement.getGeneratedKeys();

                while(rs.next()) {
                    long id = rs.getLong(1);
                    setIdUsingAnnotations(entity, id);
                }
                entityToReturn = entity;
            } catch (SQLException e) {
                throw new RuntimeException("Error occurred while executing a query. " + e.getMessage());
            }
        } else {
            entityToReturn = update(entity);
        }
        postSaveExecute(entity);
        return Optional.ofNullable(entityToReturn);
    }

    public Optional<T> findByID(long id) {
        try {
            PreparedStatement pStatement = connection.prepareStatement(getFindSQL(), PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setLong(1, id);
            ResultSet rs = pStatement.executeQuery();

            return mapResultSetToEntities(rs).getFirst();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while executing a query. " + e.getMessage());
        }
    }

    public List<Optional<T>> findAll() {
        try {
            PreparedStatement pStatement = connection.prepareStatement(getFindAllSQL());
            ResultSet rs = pStatement.executeQuery();

            return mapResultSetToEntities(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while executing a query. " + e.getMessage());
        }
    }

    public List<Optional<T>> findAllByForeignKey(String foreignKey, long id) {
        try {
            PreparedStatement pStatement = connection.prepareStatement(getFindAllByForeignKeySQL(foreignKey, id));
            ResultSet rs = pStatement.executeQuery();

            return mapResultSetToEntities(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while executing a query. " + e.getMessage());
        }
    }

    public T update(T entity) {
        return null;
    }

    public void delete(long id) {

    }

    public void delete(Long... ids) {
    }

    private boolean isIdPresent(T entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .anyMatch(f -> {
                    try {
                        f.setAccessible(true);
                        return f.get(entity) != null;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to access field: " +  e.getMessage());
                    }
                });
    }

    private void setIdUsingAnnotations(T entity, long id) {
        Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f ->  f.isAnnotationPresent(Id.class))
                .forEach(f -> {
                    try {
                        f.setAccessible(true);
                        f.set(entity, id);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Error occurred while setting a field. " + e.getMessage());
                    }
                });
    }

    abstract List<Optional<T>> mapResultSetToEntities(ResultSet rs) throws SQLException;
    abstract T mapToEntity(ResultSet rs);
    abstract void mapEntityToStatement(PreparedStatement statement, T entity);
    abstract void postSaveExecute(T entity);
    abstract String getFindSQL();
    abstract String getSaveSQL();
    abstract String getFindAllSQL();
    abstract String getFindAllByForeignKeySQL(String foreignKey, long id);
}
