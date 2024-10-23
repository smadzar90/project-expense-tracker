package repository.tests;

import org.example.db.Seeder;
import org.example.model.Category;
import org.example.model.Expense;
import org.example.model.PaymentMethod;
import org.example.model.Project;
import org.example.repository.CategoryRepository;
import org.example.repository.ExpenseRepository;
import org.example.repository.PaymentRepository;
import org.example.repository.ProjectRepository;
import org.example.repository.base.CrudRepository;
import org.example.repository.base.EntityRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseRepositoryTest {
    protected static Connection connection;
    protected static CrudRepository<Project> projectRepository;
    protected static CrudRepository<Expense> expenseRepository;
    protected static EntityRepository<Category> categoryRepository;
    protected static EntityRepository<PaymentMethod> paymentRepository;

    @BeforeAll
    static void setUpBase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:./storage/test_h2/project_expenses_db_test");
        connection.setAutoCommit(false);
        Seeder.executeDefaultQueries(connection);
        projectRepository = new ProjectRepository(connection);
        expenseRepository = new ExpenseRepository(connection);
        categoryRepository = new CategoryRepository(connection);
        paymentRepository = new PaymentRepository(connection);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @AfterEach
    void rollback() throws SQLException {
        connection.rollback();
    }
}
