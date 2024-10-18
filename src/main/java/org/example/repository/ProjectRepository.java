package org.example.repository;

import org.example.model.Expense;
import org.example.model.Project;
import org.example.repository.base.CrudRepository;

import java.sql.*;
import java.util.*;

import static org.example.utils.EntityMapperUtils.mapToExpense;
import static org.example.utils.EntityMapperUtils.mapToProject;

public class ProjectRepository extends CrudRepository<Project> {
    private final CategoryRepository categoryRepository;
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;
    private final String SAVE_SQL = "INSERT INTO PROJECT (NAME, DESCRIPTION, START_DATE, BUDGET, COMPLETED) VALUES(?, ?, ?, ?, ?);";
    private final String FIND_ALL_SQL = """
        SELECT p.ID AS PROJECT_ID, p.NAME AS PROJECT_NAME, p.DESCRIPTION AS PROJECT_DESCRIPTION,
               p.START_DATE, p.BUDGET, p.COMPLETED, e.ID AS EXPENSE_ID, e.DESCRIPTION AS EXPENSE_DESCRIPTION,
               e.AMOUNT, e.TRANSACTION_DATE, c.ID AS C_ID, c.NAME AS C_NAME, c.DESCRIPTION AS C_DESCRIPTION,
               pm.ID AS PM_ID, pm.NAME AS PM_NAME, pm.DESCRIPTION AS PM_DESCRIPTION
        FROM PROJECT p 
        LEFT JOIN EXPENSE e ON p.ID = e.PROJECT_ID 
        LEFT JOIN CATEGORY c ON e.CATEGORY_ID = c.ID 
        LEFT JOIN PAYMENT_METHOD pm ON e.PAYMENT_METHOD_ID = pm.ID;
        """;
    private final String FIND_BY_ID_SQL = FIND_ALL_SQL.substring(0, FIND_ALL_SQL.length() - 2) + " WHERE p.ID = ?";
    private final String UPDATE_SQL = "UPDATE PROJECT SET NAME=?, DESCRIPTION=?, START_DATE=?, BUDGET=?, COMPLETED=?  WHERE ID=%d";
    private final String DELETE_SQL = "DELETE FROM PROJECT WHERE ID = ?";

    public ProjectRepository(Connection connection) {
        super(connection);
        expenseRepository = new ExpenseRepository(connection);
        categoryRepository = new CategoryRepository(connection);
        paymentRepository = new PaymentRepository(connection);
    }

    @Override
    protected List<Project> mapResultSetToEntities(ResultSet rs) throws SQLException {
        Map<Project, List<Expense>> projectExpenseMap = new HashMap<>();

        while(rs.next()) {
            Project project = mapToEntity(rs);
            Expense expense = mapToExpense(rs, categoryRepository, paymentRepository);
            if(projectExpenseMap.containsKey(project)) {
                projectExpenseMap.get(project).add(expense);
            }
            projectExpenseMap.computeIfAbsent(project, k -> new ArrayList<>()).add(expense);
        }

        projectExpenseMap.forEach((key, value) -> value.forEach(key::addExpense));
        return new ArrayList<>(projectExpenseMap.keySet());
    }

    @Override
    protected Project mapToEntity(ResultSet rs) {
        return mapToProject(rs);
    }

    @Override
    protected void mapEntityToStatement(PreparedStatement statement, Project project) {
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
    protected void postSaveExecute(Project project) {
        setProjectCompletionStatus(project);
        if (!project.getExpenses().isEmpty()) {
            saveProjectExpenses(project);
        }
    }

    private void setProjectCompletionStatus(Project project) {
        project.setCompleted(project.isCompleted() != null && project.isCompleted());
    }

    private void saveProjectExpenses(Project project) {
        project.getExpenses().forEach(e -> e.setProject(project));
        expenseRepository.saveAll(project.getExpenses());
    }

    @Override
    protected String getFindSQL() {
        return FIND_BY_ID_SQL;
    }

    @Override
    protected String getSaveSQL() {
        return SAVE_SQL;
    }

    @Override
    protected String getFindAllSQL() {
        return FIND_ALL_SQL;
    }

    @Override
    protected String getFindAllByForeignKeySQL(String foreignKey) {
        return "";
    }

    @Override
    protected String getUpdateSQL(Project project) {
        return String.format(UPDATE_SQL, project.getId());
    }

    @Override
    protected String getDeleteSQL() {
        return DELETE_SQL;
    }
}
