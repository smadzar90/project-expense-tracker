package org.example.repository;

import org.example.annotations.Id;

import javax.swing.text.html.parser.Entity;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CrudRepository<T> {
    private Connection connection;

    public CrudRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveAll(Set<T> entities) {
        entities.forEach(this::save);
    }

    public T save(T entity) {
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
        return entityToReturn;
    }

    public List<T> findAll() {
        return null;
    }

    public T findByID(long id) {
        return null;
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

    abstract String getSaveSQL();
    abstract void mapEntityToStatement(PreparedStatement statement, T entity);
    abstract void postSaveExecute(T entity);
}
