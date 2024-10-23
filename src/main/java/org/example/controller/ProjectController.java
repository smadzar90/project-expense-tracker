package org.example.controller;

import com.google.gson.JsonObject;
import org.example.controller.base.CrudController;
import org.example.model.Project;
import org.example.service.ProjectService;

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
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();

        return null;
    }

    @Override
    protected void saveEntity(Project entity) {
        service.saveProject(entity);
    }
}
