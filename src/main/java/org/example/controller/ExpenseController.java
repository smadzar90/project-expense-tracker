package org.example.controller;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import org.example.controller.base.CrudController;
import org.example.model.Expense;
import org.example.model.Project;
import org.example.service.ExpenseService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

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
        Expense expense = gson.fromJson(jsonObject, Expense.class);
        Project project = new Project(jsonObject.get("projectId").getAsLong());
        expense.setProject(project);

        return expense;
    }

    @Override
    protected Expense saveEntity(Expense expense) {
        return service.saveExpense(expense);
    }

    @Override
    protected List<Expense> saveAllEntities(Set<Expense> expenses) {
        return service.saveAllExpenses(expenses);
    }

    @Override
    protected List<Expense> findAllEntities() {
        return service.findAllExpenses();
    }

    @Override
    protected Optional<Expense> findEntityById(Long id) {
        return service.findExpenseByID(id);
    }

    @Override
    protected void updateEntity(Expense expense) {
        service.updateExpense(expense);
    }

    @Override
    protected void updateAllEntities(Set<Expense> entities) {
        service.updateAllExpenses(entities);
    }

    @Override
    protected void deleteEntity(Long id) {
        service.deleteExpenseByID(id);
    }

    @Override
    protected void deleteAllEntities(Set<Long> ids) {
        service.deleteAllExpenses(ids);
    }

    @Override
    protected void handleFindAllByAttribute(HttpExchange exchange, Map<String, String> queryMap) {
        if (queryMap.containsKey("attribute") && queryMap.size() == 2) {
            String attribute = queryMap.get("attribute");

            switch (attribute) {
                case "project_id" -> handleFindAllByAttribute(exchange, queryMap, "project_id");
                case "category_id" -> handleFindAllByAttribute(exchange, queryMap, "category_id");
                case "payment_id" -> handleFindAllByAttribute(exchange, queryMap, "payment_id");
                case "transaction_date" -> handleFindAllByAttribute(exchange, queryMap, "transaction_date");
                case "amount" -> handleFindAllByAttributeGreaterOrLessThan(exchange, queryMap);
                default -> sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
            }
        } else {
            sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
        }
    }

    @Override
    protected List<Expense> getAllEntitiesByAttribute(String attribute, String value) {
        return switch (attribute) {
            case "project_id" -> service.findAllExpensesByProjectID(Long.valueOf(value));
            case "category_id" -> service.findAllExpensesByCategoryID(Long.valueOf(value));
            case "payment_id" -> service.findAllExpensesByPaymentMethodID(Long.valueOf(value));
            case "transaction_date" -> {
                LocalDate date = LocalDate.parse(value);
                yield service.findAllExpensesByTransactionDate(date);
            }
            default -> new ArrayList<>();
        };
    }

    @Override
    protected List<Expense> getAllEntitiesGreaterThan(BigDecimal value) {
        return service.findAllExpensesByAmountGreaterThen(value);
    }

    @Override
    protected List<Expense> getAllEntitiesLessThan(BigDecimal value) {
        return service.findAllExpensesByAmountLessThen(value);
    }
}