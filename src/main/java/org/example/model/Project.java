package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
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

    public Project(Long id) {
        this.id = id;
    }

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public Set<Expense> getExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        this.expenses.add(expense);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project project)) return false;
        return Objects.equals(id, project.id) && Objects.equals(name, project.name) && Objects.equals(description, project.description) && Objects.equals(startDate, project.startDate) && Objects.equals(budget, project.budget) && Objects.equals(completed, project.completed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, startDate, budget, completed);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", budget=" + budget +
                ", completed=" + completed +
                '}';
    }

    public static Project getTestProject(String name) {
        return new Project(
                name,
                "A project to launch a new corporate website.",
                LocalDate.of(2024, 11, 15),
                new BigDecimal("50000.00"));
    }
}
