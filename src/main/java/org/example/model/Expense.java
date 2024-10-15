package org.example.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Expense {
    private Long id;
    private Project project;
    private Category category;
    private PaymentMethod paymentMethod;
    private String description;
    private BigDecimal amount;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private LocalDate transactionDate;

    public Expense(Category category, PaymentMethod paymentMethod, String description, BigDecimal amount, LocalDate transactionDate) {
        this(description, amount, transactionDate);
        this.category = category;
        this.paymentMethod = paymentMethod;
    }

    public Expense(String description, BigDecimal amount, LocalDate transactionDate) {
        this.description = description;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
