package org.example.repository;

import org.example.model.Expense;
import org.example.model.Project;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

public class ProjectRepository extends CrudRepository<Project> {
    private final ExpenseRepository expenseRepository;
    private final String SAVE_SQL = "INSERT INTO PROJECT (NAME, DESCRIPTION, START_DATE, BUDGET, COMPLETED) VALUES(?, ?, ?, ?, ?);";

    public ProjectRepository(Connection connection) {
        super(connection);
        expenseRepository = new ExpenseRepository(connection);
    }

    @Override
    String getSaveSQL() {
        return SAVE_SQL;
    }

    @Override
    void mapEntityToStatement(PreparedStatement statement, Project project) {
        try {
            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(project.getStartDate().atStartOfDay()));
            statement.setBigDecimal(4, project.getBudget());
            if(project.isCompleted() != null) {
                statement.setBoolean(5, project.isCompleted());
            } else {
                statement.setObject(5, null);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while mapping the entity to statement. " + e.getMessage());
        }
    }

    @Override
    void postSaveExecute(Project entity) {
        if(!entity.getExpenses().isEmpty()) {
            expenseRepository.saveAll(entity.getExpenses());
        }
    }
}
