package org.example.controller;

import com.google.gson.JsonObject;
import org.example.controller.base.CrudController;
import org.example.model.Expense;
import org.example.model.Project;
import org.example.service.ExpenseService;

public class ExpenseController extends CrudController<Expense> {
    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @Override
    protected String getPath() {
        return "/api/expense";
    }

    @Override
    protected String getName() {
        return "Expense";
    }

    @Override
    protected Expense parseJsonToEntity(JsonObject jsonObject) {
        return null;
    }

    @Override
    protected void saveEntity(Expense entity) {

    }
}
