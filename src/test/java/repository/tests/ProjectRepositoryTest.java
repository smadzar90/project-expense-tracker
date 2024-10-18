package repository.tests;

import org.example.model.Category;
import org.example.model.Expense;
import org.example.model.PaymentMethod;
import org.example.model.Project;
import org.example.repository.ProjectRepository;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectRepositoryTest extends BaseRepositoryTest {
    @BeforeAll
    static void setUp() throws SQLException {
        setUpBase();
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
        Project savedProject = projectRepository.save(Project.getTestProject("Test name"));
        assertThat(savedProject.getId()).isGreaterThan(0);
    }

    @Test
    void canSaveProjectWithExpenses() {
        Project project = getTestProjectWithExpenses("Test name", 0);
        Project savedProject = projectRepository.save(project);
        assertThat(savedProject.getExpenses()).allMatch(e -> e.getId() != null);
    }

    @Test
    void canSaveAllProjects() {
        Set<Project> projects1 = getTestProjects();
        List<Project> projects2 = projectRepository.saveAll(projects1);
        projects2.forEach(p -> {
            assertThat(p.getId()).isNotNull();
        });
    }

    @Test
    void canFindAllProjects() {
        assertTrue(checkIfEmpty());
        Set<Project> projects2 = getTestProjects();
        projectRepository.saveAll(projects2);
        List<Project> projectsFound = projectRepository.findAll();
        assertThat(projectsFound.size()).isEqualTo(3);
        projectsFound.forEach(p -> {
            assertThat(p.getName()).isNotNull();
        });
    }

    @Test
    void canFindAllProjectsWithExpenses() {
        assertTrue(checkIfEmpty());
        Project project1 = getTestProjectWithExpenses("Test name", 0);
        Project project2 = getTestProjectWithExpenses("Test name2", 20);
        Project project3 = getTestProjectWithExpenses("Test name3", 40);
        Set<Project> projects2 = Set.of(project1, project2, project3);
        projectRepository.saveAll(projects2);
        List<Project> projectsFound = projectRepository.findAll();
        assertThat(projectsFound.size()).isEqualTo(3);
        projectsFound.forEach(p -> {
            assertThat(p.getName()).isNotNull();
            assertThat(p.getExpenses().size()).isEqualTo(3);
            p.getExpenses().forEach(
                    e -> assertTrue(e.getDescription().contains("Office Supplies"))
            );
        });
    }

    @Test
    void canFindProjectByID() {
        Set<Project> projects = getTestProjects();
        List<Project> savedProjects = projectRepository.saveAll(projects);

        savedProjects.forEach(p -> {
            Optional<Project> project = projectRepository.findByID(p.getId());
            assertThat(project).isPresent();
            assertThat(p.getId()).isEqualTo(project.get().getId());
        });
    }

    @Test
    void canUpdateProject() {
        Project savedProject = projectRepository.save(Project.getTestProject("Test1"));
        savedProject.setCompleted(true);
        savedProject.setName("New name for p1");
        projectRepository.update(savedProject);
        Optional<Project> updatedProject = projectRepository.findByID(savedProject.getId());
        assertThat(updatedProject)
                .isPresent()
                .hasValueSatisfying(p -> {
                    assertThat(p.getName()).isEqualTo("New name for p1");
                    assertThat(p.getCompleted()).isEqualTo(true);
                });

    }

    @Test
    void cannotUpdateProjectWithNoID() {
        Project project = Project.getTestProject("Test1");
        assertThrows(IllegalArgumentException.class, () -> projectRepository.update(project));
    }

    @Test
    void canUpdateAllProjects() {
        List<Project> savedProjects = projectRepository.saveAll(getTestProjects());
        savedProjects.getFirst().setName("New name for p1");
        savedProjects.get(1).setName("New name for p2");
        savedProjects.getLast().setName("New name for p3");
        Set<Project> projectSet = new HashSet<>(savedProjects);
        projectRepository.updateAll(projectSet);
        savedProjects.forEach(project -> {
            assertThat(projectRepository.findByID(project.getId()))
                    .isPresent()
                    .hasValueSatisfying(p -> assertThat(p.getName()).isEqualTo(project.getName()));
        });
    }

    @Test
    void canDeleteProject() {
        Project project = Project.getTestProject("Test1");
        projectRepository.save(project);
        projectRepository.delete(project.getId());
        assertThat(projectRepository.findByID(project.getId())).isEmpty();
    }

    @Test
    void canDeleteProjectWithExpenses() {
        Project project = getTestProjectWithExpenses("test name", 20);
        Project savedProject = projectRepository.save(project);
        projectRepository.delete(project.getId());
        assertThat(projectRepository.findByID(savedProject.getId())).isEmpty();
        savedProject.getExpenses().forEach(
                expense -> {
                    assertThat(expenseRepository.findByID(expense.getId())).isEmpty();
                }
        );
    }

    @Test
    void canDeleteAllProjects() {
        Set<Project> projects = getTestProjects();
        List<Project> savedProjects = projectRepository.saveAll(projects);
        projectRepository.deleteAll(projects);
        savedProjects.forEach(project -> {
            assertThat(projectRepository.findByID(project.getId())).isEmpty();
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

    private boolean checkIfEmpty() {
        return projectRepository.findAll().isEmpty();
    }
}
