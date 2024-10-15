package repository.tests;

import org.example.db.Seeder;
import org.example.model.Expense;
import org.example.model.Project;
import org.example.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectRepositoryTest {
    private static Connection connection;
    private static ProjectRepository repo;

    @BeforeAll
    static void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:/Users/stipanmadzar/Desktop/test_db");
        Seeder.executeDefaultQueries(connection);
        repo = new ProjectRepository(connection);
    }

    @Test
    void canSaveProject() {
        Project project = new Project(
                "New Website Launch",
                "A project to launch a new corporate website.",
                LocalDate.of(2024, 10, 15),
                new BigDecimal("50000.00")
        );
        Project savedProject = repo.save(project);
        assertThat(savedProject.getId()).isGreaterThan(0);
    }

    @Test
    void canSaveProjectWithExpenses() {
        Project project = new Project(
                "New Website",
                "A project to launch a new corporate website.",
                LocalDate.of(2024, 11, 15),
                new BigDecimal("50000.00")
        );

        Expense expense1 = new Expense("Office Supplies", new BigDecimal("150.75"), LocalDate.of(2024, 10, 15));
        Expense expense2 = new Expense("Business Lunch", new BigDecimal("45.00"), LocalDate.of(2024, 10, 15));
        Expense expense3 = new Expense("Travel Expenses", new BigDecimal("320.50"), LocalDate.of(2024, 10, 15));
        project.addExpense(expense1);
        project.addExpense(expense2);
        project.addExpense(expense3);
        Project savedProject = repo.save(project);
        System.out.println(savedProject.toString());
        assertThat(savedProject.getExpenses()).allMatch(e -> e.getId() != null);
    }
}
