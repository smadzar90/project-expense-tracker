package org.example.repository.base;

import org.example.utils.AnnotationUtils;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class CrudRepository<T> extends AnnotationUtils<T> {
    protected final Connection connection;

    public CrudRepository(Connection connection) {
        this.connection = connection;
    }

    public List<T> saveAll(Set<T> entities) {
        return entities.stream().map(this::save).collect(Collectors.toList());
    }

    public T save(T entity) {
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
            } catch (SQLException e) {
                throw new RuntimeException("Error occurred while executing a query. " + e.getMessage());
            }
        } else {
            update(entity);
        }
        postSaveExecute(entity);
        return entity;
    }

    public Optional<T> findByID(Long id) {
        try {
            PreparedStatement pStatement = connection.prepareStatement(getFindSQL(), PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setLong(1, id);
            ResultSet rs = pStatement.executeQuery();

            List<T> entities = mapResultSetToEntities(rs);
            return Optional.ofNullable(entities.isEmpty() ? null : entities.getFirst());
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while executing a query. " + e.getMessage());
        }
    }

    public List<T> findAll() {
        try {
            PreparedStatement pStatement = connection.prepareStatement(getFindAllSQL());
            ResultSet rs = pStatement.executeQuery();

            return mapResultSetToEntities(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while executing a query. " + e.getMessage());
        }
    }

    private <G> List<T> findAllByAttribute(String sql, G val) {
        try {
            PreparedStatement pStatement = connection.prepareStatement(sql);
            pStatement.setObject(1, val);
            ResultSet rs = pStatement.executeQuery();

            return mapResultSetToEntities(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while executing a query. " + e.getMessage());
        }
    }

    public <G> List<T> findAllEntitiesByAttribute(String attribute, G val) {
        return findAllByAttribute(getFindAllByAttributeSQL(attribute), val);
    }

    public <G> List<T> findAllByAttributeGreaterThan(String attribute, G val) {
        return findAllByAttribute(getFindAllByAttGreaterThan(attribute), val);
    }

    public <G> List<T> findAllByAttributeLessThan(String attribute, G val) {
        return findAllByAttribute(getFindAllByAttLessThan(attribute), val);
    }

    public void updateAll(Set<T> entities) {
        entities.forEach(this::update);
    }

    public void update(T entity) {
        if(!isIdPresent(entity)) throw new IllegalArgumentException("ID not present in entity");
        try {
            PreparedStatement pStatement = connection.prepareStatement(getUpdateSQL(entity), PreparedStatement.RETURN_GENERATED_KEYS);
            mapEntityToStatement(pStatement, entity);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while executing a query. " + e.getMessage());
        }
    }

    public void deleteAll(Set<T> entities) {
        entities.forEach(e -> delete(getIdUsingAnnotations(e)));
    }

    public void delete(Long id) {
        if(id == null) throw new IllegalArgumentException("ID not present in entity");
        try {
            PreparedStatement pStatement = connection.prepareStatement(getDeleteSQL(), PreparedStatement.RETURN_GENERATED_KEYS);
            pStatement.setLong(1, id);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while executing a query. " + e.getMessage());
        }
    }

    protected abstract List<T> mapResultSetToEntities(ResultSet rs) throws SQLException;
    protected abstract T mapToEntity(ResultSet rs);
    protected abstract void mapEntityToStatement(PreparedStatement statement, T entity);
    protected abstract void postSaveExecute(T entity);
    protected abstract String getFindSQL();
    protected abstract String getSaveSQL();
    protected abstract String getFindAllSQL();
    protected abstract String getFindAllByAttributeSQL(String attribute);
    protected abstract String getFindAllByAttGreaterThan(String attribute);
    protected abstract String getFindAllByAttLessThan(String attribute);
    protected abstract String getUpdateSQL(T entity);
    protected abstract String getDeleteSQL();
}
