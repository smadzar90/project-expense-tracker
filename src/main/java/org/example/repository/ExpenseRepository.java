package org.example.repository;

import org.example.model.Expense;
import org.example.repository.base.CrudRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.example.utils.EntityMapperUtils.mapToExpense;
import static org.example.utils.EntityMapperUtils.mapToProject;

public class ExpenseRepository extends CrudRepository<Expense> {
    private final CategoryRepository categoryRepository;
    private final PaymentRepository paymentRepository;
    private final String SAVE_SQL = "INSERT INTO EXPENSE (PROJECT_ID, CATEGORY_ID, PAYMENT_METHOD_ID, DESCRIPTION, AMOUNT, TRANSACTION_DATE) VALUES(?, ?, ?, ?, ?, ?);";
    private final String FIND_ALL_BY_FOREIGN_KEY_SQL = "SELECT * FROM EXPENSE WHERE %s = ?";
    private final String FIND_ALL_SQL= """
         SELECT p.ID AS PROJECT_ID, p.NAME AS PROJECT_NAME, p.DESCRIPTION AS PROJECT_DESCRIPTION,
                p.START_DATE, p.BUDGET, p.COMPLETED, e.ID AS EXPENSE_ID, e.DESCRIPTION AS EXPENSE_DESCRIPTION,
                e.AMOUNT, e.TRANSACTION_DATE, c.ID AS C_ID, c.NAME AS C_NAME, c.DESCRIPTION AS C_DESCRIPTION,
                pm.ID AS PM_ID, pm.NAME AS PM_NAME, pm.DESCRIPTION AS PM_DESCRIPTION
         FROM EXPENSE e
         LEFT JOIN PROJECT p ON e.PROJECT_ID = p.ID
         LEFT JOIN CATEGORY c ON e.CATEGORY_ID = c.ID
         LEFT JOIN PAYMENT_METHOD pm ON e.PAYMENT_METHOD_ID = pm.ID;
         """;
    private final String FIND_BY_ID_SQL = FIND_ALL_SQL.substring(0, FIND_ALL_SQL.length() - 2) + " WHERE e.ID = ?";
    private final String UPDATE_SQL = "UPDATE EXPENSE SET PROJECT_ID = ?, CATEGORY_ID = ?, PAYMENT_METHOD_ID = ?, DESCRIPTION = ?, AMOUNT = ?, TRANSACTION_DATE = ? WHERE ID = %d";
    private final String DELETE_SQL = "DELETE FROM EXPENSE WHERE ID = ?";

    public ExpenseRepository(Connection connection) {
        super(connection);
        categoryRepository = new CategoryRepository(connection);
        paymentRepository = new PaymentRepository(connection);
    }

    @Override
    protected List<Expense> mapResultSetToEntities(ResultSet rs) throws SQLException {
        List<Expense> expenses = new ArrayList<>();

        while(rs.next()) {
            Expense expense = mapToExpense(rs, categoryRepository, paymentRepository);
            expense.setProject(mapToProject(rs));
            expenses.add(expense);
        }
        return expenses;
    }

    @Override
    protected Expense mapToEntity(ResultSet rs) {
        return mapToExpense(rs, categoryRepository, paymentRepository);
    }

    @Override
    protected void mapEntityToStatement(PreparedStatement statement, Expense expense) {
        try {
            statement.setLong(1, expense.getProject().getId());
            statement.setLong(2, expense.getCategory().getId());
            statement.setLong(3, expense.getPaymentMethod().getId());
            statement.setString(4, expense.getDescription());
            statement.setBigDecimal(5, expense.getAmount());
            statement.setTimestamp(6, Timestamp.valueOf(expense.getTransactionDate().atStartOfDay()));
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while mapping the entity to statement. " + e.getMessage());
        }
    }

    @Override
    protected void postSaveExecute(Expense entity) {
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
        return String.format(FIND_ALL_BY_FOREIGN_KEY_SQL, foreignKey);
    }

    @Override
    protected String getUpdateSQL(Expense expense) {
        return String.format(UPDATE_SQL, expense.getId());
    }

    @Override
    protected String getDeleteSQL() {
        return DELETE_SQL;
    }
}
