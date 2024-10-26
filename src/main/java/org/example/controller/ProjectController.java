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
import java.util.*;

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
                case "name" -> handleFindAllByAttribute(exchange, queryMap, "name");
                case "start_date" -> handleFindAllByAttribute(exchange, queryMap, "start_date");
                case "budget" -> handleFindAllByAttributeGreaterOrLessThan(exchange, queryMap);
                default -> sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
            }
        } else {
            sendErrorResponse(exchange, ERR_QUERY_PARAMS_400, 400);
        }
    }

    @Override
    protected List<Project> getAllEntitiesByAttribute(String attribute, String value) {
        return switch (attribute) {
            case "name" -> service.findAllProjectsByName(value);
            case "start_date" -> {
                LocalDate date = LocalDate.parse(value);
                yield service.findAllProjectsByStartDate(date);
            }
            default -> new ArrayList<>();
        };
    }

    @Override
    protected List<Project> getAllEntitiesGreaterThan(BigDecimal value) {
        return service.findAllProjectsByBudgetGreaterThen(value);
    }

    @Override
    protected List<Project> getAllEntitiesLessThan(BigDecimal value) {
        return service.findAllProjectsByBudgetLessThan(value);
    }

    private Expense parseExpenseFromJson(JsonElement e) {
        return gson.fromJson(e, Expense.class);
    }
}
