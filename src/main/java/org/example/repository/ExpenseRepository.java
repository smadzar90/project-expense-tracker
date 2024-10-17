package org.example.repository;

import org.example.EntityMapper;
import org.example.model.Expense;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class ExpenseRepository extends CrudRepository<Expense> {
    private final CategoryRepository categoryRepository;
    private final PaymentRepository paymentRepository;
    private final String SAVE_SQL = "INSERT INTO EXPENSE (PROJECT_ID, CATEGORY_ID, PAYMENT_METHOD_ID, DESCRIPTION, AMOUNT, TRANSACTION_DATE) VALUES(?, ?, ?, ?, ?, ?);";
    private final String FIND_ALL_BY_FOREIGN_KEY_SQL = "SELECT * FROM EXPENSE WHERE %s = %d";

    public ExpenseRepository(Connection connection) {
        super(connection);
        categoryRepository = new CategoryRepository(connection);
        paymentRepository = new PaymentRepository(connection);
    }

    @Override
    List<Optional<Expense>> mapResultSetToEntities(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    public Expense mapToEntity(ResultSet rs) {
        return EntityMapper.mapToExpense(rs, categoryRepository, paymentRepository);
    }

    @Override
    void mapEntityToStatement(PreparedStatement statement, Expense expense) {
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
    void postSaveExecute(Expense entity) {
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
        return "";
    }

    @Override
    String getFindAllByForeignKeySQL(String foreignKey, long id) {
        return String.format(FIND_ALL_BY_FOREIGN_KEY_SQL, foreignKey, id);
    }
}
