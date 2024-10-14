package org.example.repository;

import org.example.model.Expense;

import java.sql.Connection;

public class ExpenseRepository extends CrudRepository<Expense> {

    public ExpenseRepository(Connection connection) {
        super(connection);
    }
}
