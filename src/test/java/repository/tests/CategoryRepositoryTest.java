package repository.tests;

import org.example.db.Connector;
import org.example.db.Seeder;
import org.example.model.Category;
import org.example.repository.CategoryRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryRepositoryTest extends Connector {
    private static Connection connection;
    private static CategoryRepository repo;

    @BeforeAll
    static void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:/Users/stipanmadzar/Desktop/db_test");
        connection.setAutoCommit(false);
        Seeder.executeDefaultQueries(connection);
        repo = new CategoryRepository(connection);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void canGetCategoryByID() {
        Category category = repo.findByID(2);
        assertThat(category.getId()).isEqualTo(2);
        assertThat(category.getName()).isEqualTo("Materials and Supplies");
    }

    @Test
    void cannotGetCategoryByInvalidID() {
        Category category1 = repo.findByID(55);
        Category category2 = repo.findByID(-2);
        assertThat(category1).isNull();
        assertThat(category2).isNull();
    }
}
