package repository.tests;

import org.example.model.Category;
import org.example.model.Expense;
import org.example.model.PaymentMethod;
import org.example.model.Project;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectRepositoryTest extends BaseRepositoryTest {

    @BeforeAll
    static void setUp() throws SQLException {
        setUpBase();
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
        Set<Project> projects2 = getTestProjects();
        projectRepository.saveAll(projects2);
        List<Project> projectsFound = projectRepository.findAll();
        assertThat(projectsFound.size()).isEqualTo(3);
        projectsFound.forEach(p -> assertThat(p.getName()).isNotNull());
    }

    @Test
    void canFindAllProjectsWithExpenses() {
        Project project1 = getTestProjectWithExpenses("Test name", 0);
        Project project2 = getTestProjectWithExpenses("Test name2", 20);
        Set<Project> projects2 = Set.of(project1, project2);
        projectRepository.saveAll(projects2);
        List<Project> projectsFound = projectRepository.findAll();
        assertThat(projectsFound.size()).isEqualTo(2);
        projectsFound.forEach(p -> {
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
        List<Project> savedProjects = projectRepository.saveAll(getTestProjects());
        Set<Long> ids = savedProjects.stream().map(Project::getId).collect(Collectors.toSet());
        projectRepository.deleteAll(ids);
        assertTrue(projectRepository.findAll().isEmpty());
    }

    @Test
    void canFindAllProjectsByName() {
        Set<Project> projects = getTestProjects();
        projects.forEach(p -> p.setName("test name"));
        projectRepository.saveAll(projects);
        List<Project> projectsFound = projectRepository.findAllEntitiesByAttribute("p.NAME", "test name");
        assertThat(projectsFound.size()).isEqualTo(3);
    }

    @Test
    void canFindAllProjectsByStartDate() {
        projectRepository.saveAll(getTestProjects());
        List<Project> projectsFound = projectRepository.findAllEntitiesByAttribute("p.START_DATE", LocalDate.of(2024, 11, 15));
        assertThat(projectsFound.size()).isEqualTo(3);
    }

    @Test
    void canFindAllProjectsWithBudgetGreaterThan() {
        projectRepository.saveAll(getTestProjects());
        List<Project> projectsFound = projectRepository.findAllByAttributeGreaterThan("p.BUDGET", new BigDecimal(30000));
        assertThat(projectsFound.size()).isEqualTo(3);
    }

    @Test
    void canFindAllProjectsWithBudgetLessThan() {
        projectRepository.saveAll(getTestProjects());
        List<Project> projectsFound = projectRepository.findAllByAttributeLessThan("p.BUDGET", new BigDecimal(100000));
        assertThat(projectsFound.size()).isEqualTo(3);
    }

    private static Project getTestProjectWithExpenses(String name, int addAmount) {
        Project project = Project.getTestProject(name);
        Category category = categoryRepository.findByID(1L).orElse(null);
        PaymentMethod paymentMethod = paymentRepository.findByID(1L).orElse(null);
        project.addExpense(Expense.getTestExpense(55000 + addAmount, category, paymentMethod));
        project.addExpense(Expense.getTestExpense(45112 + addAmount, category, paymentMethod));
        project.addExpense(Expense.getTestExpense(124424 + addAmount, category, paymentMethod));
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
