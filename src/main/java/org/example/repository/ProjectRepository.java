package org.example.repository;

import org.example.EntityMapper;
import org.example.model.Expense;
import org.example.model.Project;

import java.sql.*;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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

    public ProjectRepository(Connection connection) {
        super(connection);
        expenseRepository = new ExpenseRepository(connection);
        categoryRepository = new CategoryRepository(connection);
        paymentRepository = new PaymentRepository(connection);
    }

    @Override
    List<Optional<Project>> mapResultSetToEntities(ResultSet rs) throws SQLException {
        Map<Project, List<Expense>> projectExpenseMap = new HashMap<>();

        while(rs.next()) {
            Project project = mapToEntity(rs);
            Expense expense = EntityMapper.mapToExpense(rs, categoryRepository, paymentRepository);

            if(projectExpenseMap.containsKey(project)) {
                projectExpenseMap.get(project).add(expense);
            }
            projectExpenseMap.computeIfAbsent(project, k -> new ArrayList<>()).add(expense);
        }
        projectExpenseMap.forEach((key, value) -> value.forEach(key::addExpense));
        return projectExpenseMap.keySet().stream().map(Optional::of).collect(Collectors.toList());
    }

    @Override
    Project mapToEntity(ResultSet rs) {
        return EntityMapper.mapToProject(rs);
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
    void postSaveExecute(Project project) {
        if(project.isCompleted() == null) {
            project.setCompleted(false);
        } if(!project.getExpenses().isEmpty()) {
            project.getExpenses().forEach(e -> e.setProject(project));
            expenseRepository.saveAll(project.getExpenses());
        }
    }

    @Override
    String getFindSQL() {
        return "";
    }

    @Override
    String getSaveSQL() {
        return SAVE_SQL;
    }

    @Override
    String getFindAllSQL() {
        return FIND_ALL_SQL;
    }

    @Override
    String getFindAllByForeignKeySQL(String foreignKey, long id) {
        return "";
    }
}
