package org.example;

import org.example.model.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.repository.*;

import java.sql.Timestamp;
import java.time.ZoneId;

public class EntityMapper {
    public static Project mapToProject(ResultSet rs) {
        try {
            Project project = new Project(
                    rs.getString("PROJECT_NAME"),
                    rs.getString("PROJECT_DESCRIPTION"),
                    rs.getTimestamp("START_DATE").toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    rs.getBigDecimal("BUDGET"),
                    rs.getBoolean("COMPLETED")
            );
            project.setId(rs.getLong("PROJECT_ID"));
            return project;
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while mapping the result set to Project entity. " + e.getMessage());
        }
    }

    public static Expense mapToExpense(ResultSet rs, CategoryRepository categoryRepository, PaymentRepository paymentRepository) {
        try {
            Expense expense = new Expense(
                    categoryRepository.mapResultSetToEntity(rs),
                    paymentRepository.mapResultSetToEntity(rs),
                    rs.getString("EXPENSE_DESCRIPTION"),
                    rs.getBigDecimal("AMOUNT"),
                    null
            );
            Date date = rs.getDate("TRANSACTION_DATE");

            if(date != null) {
                expense.setTransactionDate(date.toLocalDate());
            }
            expense.setId(rs.getLong("EXPENSE_ID"));
            expense.setProject(mapToProject(rs));
            return expense;
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while mapping the result set to Expense entity. " + e.getMessage());
        }
    }
}
