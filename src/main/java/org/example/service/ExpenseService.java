package org.example.service;

import org.example.model.Expense;
import org.example.repository.ExpenseRepository;
import org.example.repository.base.CrudRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ExpenseService extends PaymentService {
    private final CrudRepository<Expense> expenseRepository;

    public ExpenseService(Connection connection) {
        super(connection);
        expenseRepository = new ExpenseRepository(connection);
    }

    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> saveAllExpenses(Set<Expense> expenses) {
        return expenseRepository.saveAll(expenses);
    }

    public Optional<Expense> findExpenseByID(Long id) {
        return expenseRepository.findByID(id);
    }

    public List<Expense> findAllExpenses() {
        return expenseRepository.findAll();
    }

    public void updateExpense(Expense expense) {
        expenseRepository.update(expense);
    }

    public void updateAllExpenses(Set<Expense> expenses) {
        expenseRepository.updateAll(expenses);
    }

    public void deleteExpenseByID(Long id) {
        expenseRepository.delete(id);
    }

    public void deleteAllExpenses(Set<Long> ids) {
        expenseRepository.deleteAll(ids);
    }

    public List<Expense> findAllExpensesByProjectID(Long id) {
        return expenseRepository.findAllEntitiesByAttribute("e.PROJECT_ID", id);
    }

    public List<Expense> findAllExpensesByCategoryID(Long id) {
        return expenseRepository.findAllEntitiesByAttribute("e.CATEGORY_ID", id);
    }

    public List<Expense> findAllExpensesByPaymentMethodID(Long id) {
        return expenseRepository.findAllEntitiesByAttribute("e.PAYMENT_EXPENSE_ID", id);
    }

    public List<Expense> findAllExpensesByTransactionDate(LocalDate date) {
        return expenseRepository.findAllEntitiesByAttribute("e.TRANSACTION_DATE", date);
    }

    public List<Expense> findAllExpensesByAmountGreaterThen(BigDecimal amount) {
        return expenseRepository.findAllByAttributeGreaterThan("e.AMOUNT", amount);
    }

    public List<Expense> findAllExpensesByAmountLessThen(BigDecimal amount) {
        return expenseRepository.findAllByAttributeLessThan("e.AMOUNT", amount);
    }
}
