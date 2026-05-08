package com.example.taskprioritiser.repsoitory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.example.taskprioritiser.TestHelper.getRandomIdExcluding;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class DoneTaskPersistenceServiceIntegrationTest {

    @Autowired
    private DoneTaskPersistenceService underTest;

    @Autowired
    private DoneTaskRepository doneTaskRepository;

    @Autowired
    private TaskRepository taskRepository;

    private Long task1Id;
    private Long task2Id;

    @BeforeEach
    void setUp() {
        task1Id = createTask("First Task");
        task2Id = createTask("Second Task");
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        doneTaskRepository.deleteAll();
    }

    @Test
    void saveDoneTask() {
        underTest.saveDoneTask(task1Id);

        DoneTaskProjection doneTask = doneTaskRepository.fetchDoneTaskByTaskId(task1Id);
        assertNotNull(doneTask);
        assertEquals(task1Id, doneTask.getTaskId());
        assertEquals("First Task", doneTask.getDescription());
        assertNotNull(doneTask.getDoneAt());
    }

    @Test
    void saveDoneTask_withNonExistingTaskID_ShouldNotThrow() {
        Long nonExistingTaskId = getRandomIdExcluding(List.of(task1Id, task2Id));
        assertDoesNotThrow(() -> underTest.saveDoneTask(nonExistingTaskId));
    }

    @Test
    void saveDoneTask_thatIsAlreadyMarkedAsDone_ShouldNotThrow() {
        underTest.saveDoneTask(task1Id);
        assertDoesNotThrow(() -> underTest.saveDoneTask(task1Id));
    }


    @Test
    void isTaskDone() {
        underTest.saveDoneTask(task1Id);
        assertTrue(underTest.isTaskDone(task1Id));
        assertFalse(underTest.isTaskDone(task2Id));
    }

    @Test
    void isTaskDone_withNonExistingTaskID_ShouldNotThrow() {
        Long nonExistingTaskId = getRandomIdExcluding(List.of(task1Id, task2Id));
        assertDoesNotThrow(() -> underTest.isTaskDone(nonExistingTaskId));
    }

    @Test
    void deleteDoneTask() {
        underTest.saveDoneTask(task1Id);
        underTest.deleteDoneTask(task1Id);
        assertFalse(underTest.isTaskDone(task1Id));
    }

    @Test
    void deleteDoneTask_withNonExistingTaskID_ShouldNotThrow() {
        Long nonExistingTaskId = getRandomIdExcluding(List.of(task1Id, task2Id));
        assertDoesNotThrow(() -> underTest.deleteDoneTask(nonExistingTaskId));
    }

    @Test
    void fetchDoneTaskByTaskId() {
        assertThat(underTest.fetchDoneTaskByTaskId(task1Id));
        underTest.saveDoneTask(task1Id);
        assertThat(underTest.fetchDoneTaskByTaskId(task1Id)).hasValueSatisfying(doneTask -> {
            assertEquals(task1Id, doneTask.getTaskId());
            assertEquals("First Task", doneTask.getDescription());
            assertNotNull(doneTask.getDoneAt());
        });
    }

    @Test
    void fetchDoneTaskByDescription() {
        assertThat(underTest.fetchDoneTaskByDescription("Second Task"));
        underTest.saveDoneTask(task2Id);
        assertThat(underTest.fetchDoneTaskByDescription("Second Task")).hasValueSatisfying(doneTask -> {
            assertEquals(task2Id, doneTask.getTaskId());
            assertEquals("Second Task", doneTask.getDescription());
            assertNotNull(doneTask.getDoneAt());
        });
    }

    @Test
    void fetchAllDoneTasks() {
        underTest.saveDoneTask(task2Id);
        underTest.saveDoneTask(task1Id);
        Long task3Id = createTask("Third Task");

        List<DoneTaskProjection> doneTasks = underTest.fetchAllDoneTasks();
        assertThat(doneTasks).hasSize(2);
        assertThat(doneTasks).extracting(DoneTaskProjection::getTaskId)
                .doesNotContain(task3Id);
    }

    @Test
    void fetchAllNotDoneTasks() {
        underTest.saveDoneTask(task1Id);
        Long task3Id = createTask("Third Task");

        List<TaskEntity> doneTasks = underTest.fetchAllNotDoneTasks();
        assertThat(doneTasks).hasSize(2);
        assertThat(doneTasks).extracting(TaskEntity::getTaskId)
                .containsExactlyInAnyOrder(task2Id, task3Id);
    }

    private Long createTask(String description) {
        TaskEntity task = new TaskEntity(description, 3, 4, 6, Instant.now().plusSeconds(3600));
        TaskEntity saved = taskRepository.save(task);
        return saved.getTaskId();

    }
}