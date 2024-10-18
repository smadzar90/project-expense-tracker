package repository.tests;

import org.example.model.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryRepositoryTest extends BaseRepositoryTest {

    @BeforeAll
    static void setUp() throws SQLException {
        setUpBase();
    }

    @Test
    void canGetCategoryByID() {
        Optional<Category> category = categoryRepository.findByID(2);
        assertThat(category).isPresent();
        assertThat(category.get().getId()).isEqualTo(2);
        assertThat(category.get().getName()).isEqualTo("Materials and Supplies");
    }

    @Test
    void cannotGetCategoryByInvalidID() {
        Optional<Category> category1 = categoryRepository.findByID(55);
        Optional<Category> category2 = categoryRepository.findByID(-2);
        assertThat(category1).isEmpty();
        assertThat(category2).isEmpty();
    }
}
