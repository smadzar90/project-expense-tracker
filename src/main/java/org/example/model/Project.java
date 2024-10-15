package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.example.annotations.Id;

public class Project {
    @Id
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private BigDecimal budget;
    private Boolean completed;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Set<Expense> expenses = new HashSet<>();

    public Project(String name, String description, LocalDate startDate, BigDecimal budget) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.budget = budget;
    }

    public Project(String name, String description, LocalDate startDate, BigDecimal budget, Boolean completed) {
        this(name, description, startDate, budget);
        this.completed = completed;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public Set<Expense> getExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        this.expenses.add(expense);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Project{" +
                "Id=" + id +
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
