package repository.tests;

import org.example.model.Category;
import org.example.model.Expense;
import org.example.model.PaymentMethod;
import org.example.model.Project;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExpenseRepositoryTest extends BaseRepositoryTest {

    @BeforeAll
    static void setUp() throws SQLException {
        setUpBase();
    }

    @Test
    void canSaveExpense() {
       Expense expense = expenseRepository.save(getExpense(40000));
       assertThat(expense.getId()).isGreaterThan(0);
    }

    @Test
    void canSaveAllExpenses() {
        List<Expense> expenses = expenseRepository.saveAll(getTestExpenses());
        expenses.forEach(expense -> assertThat(expense.getId()).isNotNull());
    }

    @Test
    void canFindAllExpenses() {
        assertTrue(checkIfEmpty());
        Set<Expense> expenses = getTestExpenses();
        List<Expense> savedExpenses = expenseRepository.saveAll(expenses);
        savedExpenses.forEach(expense -> {
            assertThat(expense.getCategory().getId()).isEqualTo(1);
            assertThat(expense.getPaymentMethod().getId()).isEqualTo(1);
            assertThat(expense.getId()).isGreaterThan(0);
        });
    }

    @Test
    void canFindExpenseByID() {
        Expense savedExpense1 = expenseRepository.save(getExpense(30000));
        Expense savedExpense2 = expenseRepository.save(getExpense(13000));
        assertThat(expenseRepository.findByID(savedExpense1.getId()))
                .isPresent()
                .hasValueSatisfying(expense -> {
                    assertThat(expense.getAmount()).isEqualTo(new BigDecimal("30000.00"));
                });
        assertThat(expenseRepository.findByID(savedExpense2.getId()))
                .isPresent()
                .hasValueSatisfying(expense -> {
                    assertThat(expense.getAmount()).isEqualTo(new BigDecimal("13000.00"));
                });
    }

    @Test
    void canUpdateExpense() {
        Expense savedExpense = expenseRepository.save(getExpense(30000));
        savedExpense.setAmount(new BigDecimal(100000));
        expenseRepository.update(savedExpense);
        Optional<Expense> updatedExpense = expenseRepository.findByID(savedExpense.getId());
        assertThat(updatedExpense).isPresent();
        assertThat(updatedExpense.get().getAmount()).isEqualTo(new BigDecimal("100000.00"));
    }

    @Test
    void cannotUpdateExpenseWithNoID() {
        Expense expense = getExpense(20000);
        assertThrows(IllegalArgumentException.class, () -> expenseRepository.update(expense));
    }

    @Test
    void canUpdateAllExpense() {
        Set<Expense> expenses = getTestExpenses();
        List<Expense> savedExpenses = expenseRepository.saveAll(expenses);
        Set<Expense> expensesSet = savedExpenses
                .stream()
                .peek(expense -> expense.setAmount(expense.getAmount().add(new BigDecimal(1000))))
                .collect(Collectors.toSet());
        expenseRepository.updateAll(expensesSet);
        expensesSet.forEach(expense -> {
            Optional<Expense> updatedExpense = expenseRepository.findByID(expense.getId());
            assertThat(updatedExpense).isPresent();
            assertThat(updatedExpense.get().getAmount()).isEqualByComparingTo(expense.getAmount());
        });
    }

    @Test
    void canDeleteExpense() {
        Expense expense = expenseRepository.save(getExpense(40000));
        expenseRepository.delete(expense.getId());
        assertThat(expenseRepository.findByID(expense.getId())).isNotPresent();
    }

    @Test
    void canDeleteAllExpenses() {
        List<Expense> expenses = expenseRepository.saveAll(getTestExpenses());
        expenseRepository.deleteAll(new HashSet<>(expenses));
        assertTrue(checkIfEmpty());
    }

    private static Set<Expense> getTestExpenses() {
        return Set.of(
                getExpense(50000),
                getExpense(25000),
                getExpense(10000),
                getExpense(100000)
        );
    }

    private static Expense getExpense(Integer testAmount) {
        Project savedProject = projectRepository.save(Project.getTestProject("test project"));
        Category category = categoryRepository.findByID(1).orElse(null);
        PaymentMethod paymentMethod = paymentRepository.findByID(1).orElse(null);
        Expense expense = Expense.getTestExpense(testAmount, category, paymentMethod);
        expense.setProject(savedProject);
        return expense;
    }

    private static boolean checkIfEmpty() {
        return expenseRepository.findAll().isEmpty();
    }
}
