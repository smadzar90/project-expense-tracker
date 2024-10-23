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
    private CrudRepository<Expense> expenseRepository;

    public ExpenseService(Connection connection) {
        super(connection);
        expenseRepository = new ExpenseRepository(connection);
    }

    Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    List<Expense> saveAllExpenses(Set<Expense> expenses) {
        return expenseRepository.saveAll(expenses);
    }

    Optional<Expense> findExpenseByID(Long id) {
        return expenseRepository.findByID(id);
    }

    List<Expense> findAllExpenses() {
        return expenseRepository.findAll();
    }

    void updateExpense(Expense expense) {
        expenseRepository.update(expense);
    }

    void updateAllExpenses(Set<Expense> expenses) {
        expenseRepository.updateAll(expenses);
    }

    void deleteExpenseByID(Long id) {
        expenseRepository.delete(id);
    }

    void deleteAllExpenses(Set<Expense> expenses) {
        expenseRepository.deleteAll(expenses);
    }

    List<Expense> findAllExpensesByProjectID(Long id) {
        return expenseRepository.findAllEntitiesByAttribute("e.PROJECT_ID", id);
    }

    List<Expense> findAllExpensesByCategoryID(Long id) {
        return expenseRepository.findAllEntitiesByAttribute("e.CATEGORY_ID", id);
    }

    List<Expense> findAllExpensesByPaymentMethodID(Long id) {
        return expenseRepository.findAllEntitiesByAttribute("e.PAYMENT_EXPENSE_ID", id);
    }

    List<Expense> findAllExpensesByTransactionDate(LocalDate date) {
        return expenseRepository.findAllEntitiesByAttribute("e.TRANSACTION_DATE", date);
    }

    List<Expense> findAllExpensesByAmountGreaterThen(BigDecimal amount) {
        return expenseRepository.findAllByAttributeGreaterThan("e.AMOUNT", amount);
    }

    List<Expense> findAllExpensesByAmountLessThen(BigDecimal amount) {
        return expenseRepository.findAllByAttributeLessThan("e.AMOUNT", amount);
    }
}
