package org.example.repository;

import org.example.model.Category;
import org.example.repository.base.EntityRepository;

import java.sql.*;

public class CategoryRepository extends EntityRepository<Category> {
    private final String FIND_ALL_SQL = "SELECT ID AS C_ID, NAME AS C_NAME, DESCRIPTION AS C_DESCRIPTION FROM CATEGORY;";
    private final String FIND_SQL = FIND_ALL_SQL.substring(0, FIND_ALL_SQL.length() - 1).concat(" WHERE ID=?;");

    public CategoryRepository(Connection connection) {
        super(connection);
    }

    @Override
    public Category mapResultSetToEntity(ResultSet rs) {
        try {
            Category category = new Category(rs.getString("C_NAME"), rs.getString("C_DESCRIPTION"));
            category.setId(rs.getLong("C_ID"));
            return category;
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while mapping the result set to Category entity. " + e.getMessage());
        }
    }

    @Override
    protected String getFindSQL() {
        return FIND_SQL;
    }

    @Override
    protected String getFindAllSQL() {
        return FIND_ALL_SQL;
    }
}
