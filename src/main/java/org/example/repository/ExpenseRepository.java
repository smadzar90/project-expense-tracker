package org.example.repository;

import org.example.model.Expense;
import org.example.repository.base.CrudRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.example.repository.ExpenseSQL.*;
import static org.example.utils.EntityMapperUtils.mapToExpense;
import static org.example.utils.EntityMapperUtils.mapToProject;

public class ExpenseRepository extends CrudRepository<Expense> {
    private final CategoryRepository categoryRepository;
    private final PaymentRepository paymentRepository;

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
        return ExpenseSQL.FIND_BY_ID_SQL;
    }

    @Override
    protected String getSaveSQL() {
        return SAVE_SQL;
    }

    @Override
    protected String getFindAllSQL() {
        return ExpenseSQL.FIND_ALL_SQL;
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
    protected String getUpdateSQL(Expense expense) {
        return String.format(UPDATE_SQL, expense.getId());
    }

    @Override
    protected String getDeleteSQL() {
        return DELETE_SQL;
    }
}
