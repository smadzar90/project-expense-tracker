package org.example.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import org.example.controller.base.CrudController;
import org.example.model.Expense;
import org.example.model.Project;
import org.example.service.ProjectService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ProjectController extends CrudController<Project> {
    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @Override
    protected String getPath() {
        return "/api/project";
    }

    @Override
    protected String getName() {
        return "Project";
    }

    @Override
    protected Project parseJsonToEntity(JsonObject jsonObject) {
        Project project = gson.fromJson(jsonObject, Project.class);
        JsonArray expenses = jsonObject.getAsJsonArray("expenses");

        expenses.forEach(expense -> project.addExpense(parseExpenseFromJson(expense)));
        return project;
    }

    private Expense parseExpenseFromJson(JsonElement e) {
        return gson.fromJson(e, Expense.class);
    }

    @Override
    protected Project saveEntity(Project project) {
        return service.saveProject(project);
    }

    @Override
    protected List<Project> saveAllEntities(Set<Project> projects) {
        return service.saveAllProjects(projects);
    }

    @Override
    protected List<Project> findAllEntities() {
        return service.findAllProjects();
    }

    @Override
    protected Optional<Project> findEntityById(Long id) {
        return service.findProjectByID(id);
    }

    @Override
    protected void updateEntity(Project project) {
        service.updateProject(project);
    }

    @Override
    protected void updateAllEntities(Set<Project> projects) {
        service.updateAllProjects(projects);
    }

    @Override
    protected void deleteEntity(Long id) {
        service.deleteProjectsByID(id);
    }

    @Override
    protected void deleteAllEntities(Set<Long> ids) {
        service.deleteAllProjects(ids);
    }

    @Override
    protected void handleFindAllByAttribute(HttpExchange exchange, Map<String, String> queryMap) {
        if(queryMap.containsKey("attribute") && queryMap.size() == 2) {
            String attribute = queryMap.get("attribute");

            switch (attribute) {
                case "name" -> handleFindAllByName(exchange, queryMap);
                case "start_date" -> handleFindAllByStartDate(exchange, queryMap);
                case "budget" -> handleFindAllByBudget(exchange, queryMap);
                default -> sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
            }
        } else {
            sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
        }
    }

    private void handleFindAllByName(HttpExchange exchange, Map<String, String> queryMap) {
        if(queryMap.containsKey("value")) {
            String value = queryMap.get("value");
            List<Project> projectsByName = service.findAllProjectsByName(value);
            sendJsonResponse(exchange, projectsByName, 200);
        } else {
            sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
        }
    }

    private void handleFindAllByStartDate(HttpExchange exchange, Map<String, String> queryMap) {
        if(queryMap.containsKey("value")) {
            String value = queryMap.get("value");
            LocalDate date = LocalDate.parse(value);
            List<Project> projectsByStartDate = service.findAllProjectsByStartDate(date);
            sendJsonResponse(exchange, projectsByStartDate, 200);
        } else {
            sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
        }
    }

    private void handleFindAllByBudget(HttpExchange exchange, Map<String, String> queryMap) {
        if(queryMap.containsKey("greater_than")) {
            BigDecimal value = new BigDecimal(queryMap.get("greater_than"));
            handleFindByAllBudgetGreaterThan(exchange, value);

        } else if(queryMap.containsKey("less_than")) {
            BigDecimal value = new BigDecimal(queryMap.get("less_than"));
            handleFindByAllBudgetLessThan(exchange, value);

        } else {
            sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
        }
    }

    private void handleFindByAllBudgetGreaterThan(HttpExchange exchange, BigDecimal value) {
        List<Project> projectsByName = service.findAllProjectsByBudgetGreaterThen(value);
        sendJsonResponse(exchange, projectsByName, 200);
    }

    private void handleFindByAllBudgetLessThan(HttpExchange exchange, BigDecimal value) {
        List<Project> projectsByName = service.findAllProjectsByBudgetLessThan(value);
        sendJsonResponse(exchange, projectsByName, 200);
    }
}
