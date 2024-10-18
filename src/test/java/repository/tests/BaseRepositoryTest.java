package repository.tests;

import org.example.db.Seeder;
import org.example.repository.CategoryRepository;
import org.example.repository.ExpenseRepository;
import org.example.repository.PaymentRepository;
import org.example.repository.ProjectRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseRepositoryTest {
    protected static Connection connection;
    protected static ProjectRepository projectRepository;
    protected static ExpenseRepository expenseRepository;
    protected static CategoryRepository categoryRepository;
    protected static PaymentRepository paymentRepository;

    @BeforeAll
    static void setUpBase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:/Users/stipanmadzar/Desktop/db_test");
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
