package repository.tests;

import org.example.db.Seeder;
import org.example.model.Category;
import org.example.model.PaymentMethod;
import org.example.repository.CategoryRepository;
import org.example.repository.PaymentRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentRepositoryTest {
    private static Connection connection;
    private static PaymentRepository repo;

    @BeforeAll
    static void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:/Users/stipanmadzar/Desktop/db_test");
        connection.setAutoCommit(false);
        Seeder.executeDefaultQueries(connection);
        repo = new PaymentRepository(connection);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void canGetPaymentByID() {
        PaymentMethod payment = repo.findByID(2);
        assertThat(payment.getId()).isEqualTo(2);
        assertThat(payment.getName()).isEqualTo("Personal Credit Card");
    }

    @Test
    void cannotGetPaymentByInvalidID() {
        PaymentMethod payment1 = repo.findByID(55);
        PaymentMethod payment2 = repo.findByID(-2);
        assertThat(payment1).isNull();
        assertThat(payment2).isNull();
    }
}
