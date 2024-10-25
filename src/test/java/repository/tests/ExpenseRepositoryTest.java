package repository.tests;

import org.example.model.Category;
import org.example.model.Expense;
import org.example.model.PaymentMethod;
import org.example.model.Project;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
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
        expenseRepository.saveAll(getTestExpenses());
        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses.size()).isEqualTo(4);
    }

    @Test
    void canFindExpenseByID() {
        Expense expense = expenseRepository.save(getExpense(30000));
        assertThat(expenseRepository.findByID(expense.getId()))
                .isPresent()
                .hasValueSatisfying(e -> {
                    assertThat(e.getAmount()).isEqualTo(new BigDecimal("30000.00"));
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
        Set<Long> ids = expenses.stream().map(Expense::getId).collect(Collectors.toSet());
        expenseRepository.deleteAll(ids);
        assertTrue((expenseRepository.findAll()).isEmpty());
    }

    @Test
    void canFindAllExpensesByProject() {
        Expense expense1 = getExpense(200000);
        Expense expense2 = getExpense(250000);
        Project savedProject = projectRepository.save(Project.getTestProject("test project"));
        expense1.setProject(savedProject);
        expense2.setProject(savedProject);
        expenseRepository.saveAll(Set.of(expense1, expense2));
        List<Expense> expenses = expenseRepository.findAllEntitiesByAttribute("e.PROJECT_ID", savedProject.getId());
        assertThat(expenses.size()).isEqualTo(2);
        expenses.forEach(e -> assertThat(e.getProject().getName()).isEqualTo("test project"));
    }

    @Test
    void canFindAllExpensesByCategory() {
        expenseRepository.saveAll(getTestExpenses());
        List<Expense> foundExpenses = expenseRepository.findAllEntitiesByAttribute("e.CATEGORY_ID", 1);
        assertThat(foundExpenses.size()).isEqualTo(4);
        foundExpenses.forEach(e -> assertThat(e.getCategory().getName()).isEqualTo("Labor Costs"));
    }

    @Test
    void canFindAllExpensesByPaymentMethod() {
        expenseRepository.saveAll(getTestExpenses());
        List<Expense> foundExpenses = expenseRepository.findAllEntitiesByAttribute("e.PAYMENT_METHOD_ID", 1);
        assertThat(foundExpenses.size()).isEqualTo(4);
        foundExpenses.forEach(e -> assertThat(e.getPaymentMethod().getName()).isEqualTo("Company Credit Card"));
    }

    @Test
    void canFindAllExpensesByTransactionDate() {
        expenseRepository.saveAll(getTestExpenses());
        List<Expense> foundExpenses = expenseRepository.findAllEntitiesByAttribute("e.TRANSACTION_DATE", LocalDate.of(2024, 10, 15));
        assertThat(foundExpenses.size()).isEqualTo(4);
    }

    @Test
    void canFindAllExpensesByAmountGreaterThen() {
        expenseRepository.saveAll(getTestExpenses());
        List<Expense> foundExpenses = expenseRepository.findAllByAttributeGreaterThan("e.AMOUNT", new BigDecimal(30000));
        assertThat(foundExpenses.size()).isEqualTo(2);
    }

    @Test
    void canFindAllExpensesByAmountLessThen() {
        expenseRepository.saveAll(getTestExpenses());
        List<Expense> foundExpenses = expenseRepository.findAllByAttributeLessThan("e.AMOUNT", new BigDecimal(30000));
        assertThat(foundExpenses.size()).isEqualTo(2);
    }

    private static Expense getExpense(Integer testAmount) {
        Project savedProject = projectRepository.save(Project.getTestProject("test project"));
        Category category = categoryRepository.findByID(1L).orElse(null);
        PaymentMethod paymentMethod = paymentRepository.findByID(1L).orElse(null);
        Expense expense = Expense.getTestExpense(testAmount, category, paymentMethod);
        expense.setProject(savedProject);
        return expense;
    }

    private static Set<Expense> getTestExpenses() {
        return Set.of(
                getExpense(50000),
                getExpense(25000),
                getExpense(10000),
                getExpense(100000)
        );
    }
}
