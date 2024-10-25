package org.example.model;

import org.example.annotations.Id;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Expense {
    @Id
    private Long id;
    private Project project;
    private Category category;
    private PaymentMethod paymentMethod;
    private String description;
    private BigDecimal amount;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private LocalDate transactionDate;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Expense(Long categoryId, Long paymentId, String description, BigDecimal amount, LocalDate transactionDate) {
        this(description, amount, transactionDate);
        this.category = new Category(categoryId);
        this.paymentMethod = new PaymentMethod(paymentId);
    }

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

    public Category getCategory() {
        return category;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", projectID=" + project.getId() +
                ", categoryID=" + category.getId() +
                ", paymentMethodID=" + paymentMethod.getId() +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expense expense)) return false;
        return Objects.equals(project, expense.project) && Objects.equals(category, expense.category) && Objects.equals(paymentMethod, expense.paymentMethod) && Objects.equals(description, expense.description) && Objects.equals(amount, expense.amount) && Objects.equals(transactionDate, expense.transactionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, category, paymentMethod, description, amount, transactionDate);
    }

    public static Expense getTestExpense(Integer testAmount, Category category, PaymentMethod paymentMethod) {
        return new Expense(category, paymentMethod, "Office Supplies", new BigDecimal(testAmount), LocalDate.of(2024, 10, 15));
    }
}
