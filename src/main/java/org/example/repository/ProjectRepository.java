package org.example.repository;

import org.example.model.Project;

import java.sql.Connection;

public class ProjectRepository extends CrudRepository<Project> {

    public ProjectRepository(Connection connection) {
        super(connection);
    }
}
