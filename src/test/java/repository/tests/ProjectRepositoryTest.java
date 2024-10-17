package repository.tests;

import org.example.db.Seeder;
import org.example.model.Category;
import org.example.model.Expense;
import org.example.model.PaymentMethod;
import org.example.model.Project;
import org.example.repository.CategoryRepository;
import org.example.repository.PaymentRepository;
import org.example.repository.ProjectRepository;
import org.junit.jupiter.api.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectRepositoryTest {
    private static Connection connection;
    private static ProjectRepository repo;
    private static CategoryRepository categoryRepository;
    private static PaymentRepository paymentRepository;

    @BeforeAll
    static void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:/Users/stipanmadzar/Desktop/db_test");
        connection.setAutoCommit(false);
        Seeder.executeDefaultQueries(connection);
        repo = new ProjectRepository(connection);
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


    @Test
    void canSaveProject() {
        Optional<Project> savedProject = repo.save(Project.getTestProject("Test name"));
        assertTrue(savedProject.isPresent());
        assertThat(savedProject.get().getId()).isGreaterThan(0);
    }

    @Test
    void canSaveProjectWithExpenses() {
        Project project = getTestProjectWithExpenses("Test name", 0);
        Optional<Project> savedProject = repo.save(project);
        assertTrue(savedProject.isPresent());
        assertThat(savedProject.get().getExpenses()).allMatch(e -> e.getId() != null);
    }

    @Test
    void canSaveAllProjects() {
        Set<Project> projects1 = getTestProjects();
        List<Optional<Project>> projects2 = repo.saveAll(projects1);
        projects2.forEach(p -> {
            assertThat(p).isPresent();
            assertThat(p.get().getId()).isNotNull();
        });
    }

    @Test
    void canFindAllProjects() {
        List<Optional<Project>> projects = repo.findAll();
        assertTrue(projects.isEmpty());
        Set<Project> projects2 = getTestProjects();
        repo.saveAll(projects2);
        List<Optional<Project>> projectsFound = repo.findAll();
        assertThat(projectsFound.size()).isEqualTo(3);
        projectsFound.forEach(p -> {
            assertThat(p).isPresent();
            assertThat(p.get().getName()).isNotNull();
        });
    }

    @Test
    void canFindAllProjectsWithExpenses() {
        List<Optional<Project>> projects = repo.findAll();
        assertTrue(projects.isEmpty());
        Project project1 = getTestProjectWithExpenses("Test name", 0);
        Project project2 = getTestProjectWithExpenses("Test name2", 20);
        Project project3 = getTestProjectWithExpenses("Test name3", 40);
        Set<Project> projects2 = Set.of(project1, project2, project3);
        repo.saveAll(projects2);
        List<Optional<Project>> projectsFound = repo.findAll();
        assertThat(projectsFound.size()).isEqualTo(3);
        projectsFound.forEach(p -> {
            System.out.println(p);
            System.out.println(p.get().getExpenses());
            assertThat(p).isPresent();
            assertThat(p.get().getName()).isNotNull();
            assertThat(p.get().getExpenses().size()).isEqualTo(3);
        });
    }



    private static Project getTestProjectWithExpenses(String name, int addAmount) {
        Project project = Project.getTestProject(name);
        Category category = categoryRepository.findByID(1);
        PaymentMethod paymentMethod = paymentRepository.findByID(1);

        project.addExpense(Expense.getTestExpense(55000, category, paymentMethod));
        project.addExpense(Expense.getTestExpense(45112, category, paymentMethod));
        project.addExpense(Expense.getTestExpense(124424, category, paymentMethod));
        return project;
    }

    private static Set<Project> getTestProjects() {
        return Set.of(
                Project.getTestProject("Test1"),
                Project.getTestProject("Test2"),
                Project.getTestProject("Test3")
        );
    }
}
