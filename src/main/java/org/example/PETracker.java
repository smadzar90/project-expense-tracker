package org.example;

import org.example.db.Connector;
import org.example.service.ExpenseService;
import org.example.service.ProjectService;

public class PETracker extends Connector {
    private final ProjectService projectService;
    private final ExpenseService expenseService;

    public PETracker() {
        super();
        projectService = new ProjectService(this.connection);
        expenseService = new ExpenseService(this.connection);
    }

    public PETracker(String path) {
        super(path);
        projectService = new ProjectService(this.connection);
        expenseService = new ExpenseService(this.connection);
    }
}
