package org.example.repository;

import org.example.model.Expense;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ExpenseRepository extends CrudRepository<Expense> {

    public ExpenseRepository(Connection connection) {
        super(connection);
    }

    @Override
    String getSaveSQL() {
        return "";
    }

    @Override
    void mapEntityToStatement(PreparedStatement statement, Expense entity) {
        //if project_id null throw exception
    }

    @Override
    void postSaveExecute(Expense entity) {
    }
}
