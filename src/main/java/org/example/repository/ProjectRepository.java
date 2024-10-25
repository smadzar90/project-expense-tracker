package org.example.repository;

import org.example.model.Expense;
import org.example.model.Project;
import org.example.repository.base.CrudRepository;

import java.sql.*;
import java.util.*;

import static org.example.repository.ProjectSQL.*;
import static org.example.utils.EntityMapper.mapToExpense;
import static org.example.utils.EntityMapper.mapToProject;

public class ProjectRepository extends CrudRepository<Project> {
    private final CategoryRepository categoryRepository;
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;

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

            projectExpenseMap.computeIfAbsent(project, _ -> new ArrayList<>());

            if(expense.getId() > 0) {
                projectExpenseMap.get(project).add(expense);
            }
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
            if(project.getCompleted() != null) {
                statement.setBoolean(5, project.getCompleted());
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
        project.setCompleted(project.getCompleted() != null && project.getCompleted());
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
    protected String getFindAllByAttributeSQL(String attribute) {
        return String.format(FIND_ALL_BY_ATTRIBUTE_SQL, attribute);
    }

    @Override
    protected String getFindAllByAttGreaterThan(String attribute) {
        return String.format(FIND_ALL_BY_ATT_GREATER_THAN_SQL, attribute);
    }

    @Override
    protected String getFindAllByAttLessThan(String attribute) {
        return String.format(FIND_ALL_BY_ATT_SMALLER_THAN_SQL, attribute);
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
