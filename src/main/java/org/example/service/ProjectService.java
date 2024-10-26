package org.example.service;

import org.example.model.Project;
import org.example.repository.ProjectRepository;
import org.example.repository.base.CrudRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ProjectService extends ExpenseService {
    private final CrudRepository<Project> projectRepository;

    public ProjectService(Connection connection) {
        super(connection);
        projectRepository = new ProjectRepository(connection);
    }

    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    public List<Project> saveAllProjects(Set<Project> projects) {
        return projectRepository.saveAll(projects);
    }

    public Optional<Project> findProjectByID(Long id) {
        return projectRepository.findByID(id);
    }

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public void updateProject(Project project) {
        projectRepository.update(project);
    }

    public void updateAllProjects(Set<Project> projects) {
        projectRepository.updateAll(projects);
    }

    public void deleteProjectsByID(Long id) {
        projectRepository.delete(id);
    }

    public void deleteAllProjects(Set<Long> ids) {
        projectRepository.deleteAll(ids);
    }

    public List<Project> findAllProjectsByName(String name) {
        return projectRepository.findAllEntitiesByAttribute("p.NAME", name);
    }

    public List<Project> findAllProjectsByStartDate(LocalDate startDate) {
        return projectRepository.findAllEntitiesByAttribute("p.START_DATE", startDate);
    }

    public List<Project> findAllProjectsByBudgetGreaterThen(BigDecimal budget) {
        return projectRepository.findAllByAttributeGreaterThan("p.BUDGET", budget);
    }

    public List<Project> findAllProjectsByBudgetLessThan(BigDecimal budget) {
        return projectRepository.findAllByAttributeLessThan("p.BUDGET", budget);
    }
}
