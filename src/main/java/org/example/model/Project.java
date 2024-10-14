package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.example.annotations.Id;

public class Project {
    @Id
    private long Id;
    private String name;
    private String description;
    private LocalDate startDate;
    private BigDecimal budget;
    private boolean completed;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    public Project(long Id, String name, String description, LocalDate startDate, BigDecimal budget) {
        this.Id = Id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.budget = budget;
    }

    public Project(long Id, String name, String description, LocalDate startDate, BigDecimal budget, boolean completed) {
        this.Id = Id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.budget = budget;
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Project{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", budget=" + budget +
                ", completed=" + completed +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }
}
