package org.example.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import org.example.controller.base.CrudController;
import org.example.model.Expense;
import org.example.model.Project;
import org.example.service.ExpenseService;

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
        if(queryMap.containsKey("attribute") && queryMap.size() == 2) {
            String attribute = queryMap.get("attribute");

            switch (attribute) {
                case "project_id" -> handleFindAllByForeignKey(exchange, queryMap, "project");
                case "category_id" -> handleFindAllByForeignKey(exchange, queryMap, "category");
                case "payment_id" -> handleFindAllByForeignKey(exchange, queryMap, "payment");
                case "transaction_date" -> handleFindAllByTransactionDate(exchange, queryMap);
                case "amount" -> handleFindAllByAmount(exchange, queryMap);
                default -> sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
            }
        } else {
            sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
        }
    }

    private void handleFindAllByForeignKey(HttpExchange exchange, Map<String, String> queryMap, String key) {
        if(queryMap.containsKey("value")) {
            Long id = Long.valueOf(queryMap.get("value"));

            List<Expense> expenses = getAllExpensesByForeignKey(key, id);
            sendJsonResponse(exchange, expenses, 200);
        } else {
            sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
        }
    }

    private List<Expense> getAllExpensesByForeignKey(String key, Long id) {
        return switch (key) {
            case "project" -> service.findAllExpensesByProjectID(id);
            case "category" -> service.findAllExpensesByCategoryID(id);
            case "payment" -> service.findAllExpensesByPaymentMethodID(id);
            default -> new ArrayList<>();
        };
    }

    private void handleFindAllByAmount(HttpExchange exchange, Map<String, String> queryMap) {
    }

    private void handleFindAllByTransactionDate(HttpExchange exchange, Map<String, String> queryMap) {
    }
}
