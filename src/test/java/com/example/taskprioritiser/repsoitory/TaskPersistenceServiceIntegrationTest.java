package com.example.taskprioritiser.repsoitory;

import com.example.taskprioritiser.internal.service.repsoitory.entity.TaskEntity;
import com.example.taskprioritiser.internal.service.repsoitory.TaskPersistenceService;
import com.example.taskprioritiser.internal.service.repsoitory.TaskRepository;
import com.example.taskprioritiser.internal.service.model.ScoreType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TaskPersistenceServiceIntegrationTest {

    @Autowired
    private TaskPersistenceService taskPersistenceService;

    @Autowired
    private TaskRepository taskRepository;



    @Test
    // this also tests the get method
    void createTask_ShouldInsertIntoDatabase() {

        // TODO test fetcg by id when nothing to fetch

        // Arrange
        TaskEntity taskEntity = new TaskEntity("Integration Test Task", 5, 4, 3, Instant.now().plusSeconds(3600));

        // Act
        Long taskId = taskPersistenceService.createTask(taskEntity);

        // Assert
        assertNotNull(taskId);
        TaskEntity savedTask = taskRepository.fetchTaskById(taskId);
        assertNotNull(savedTask);
        assertEquals("Integration Test Task", savedTask.getDescription());
        assertEquals(5, savedTask.getEffort());
        assertEquals(4, savedTask.getImpact());
        assertEquals(3, savedTask.getUrgency());
    }
    @Test
    void createTask_ShouldThrowException_WhenDescriptionNotUnique() {
        // TODO test fetcg by descriotion when nothing to fetch

        // Arrange
        TaskEntity task1 = new TaskEntity("Unique Desc", 1, 1, 1, Instant.now().plusSeconds(3600));
        taskPersistenceService.createTask(task1);
        TaskEntity task2 = new TaskEntity("Unique Desc", 2, 2, 2, Instant.now().plusSeconds(3600));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskPersistenceService.createTask(task2));
        assertEquals("Description must be unique", exception.getMessage());
    }


    // TODO - add more tasks
    @Test
    void getAllTasks_ShouldReturnFromDatabase() {
        // Arrange
        TaskEntity task1 = new TaskEntity("Task 1", 2, 3, 4, Instant.now().plusSeconds(3600));
        TaskEntity task2 = new TaskEntity("Task 2", 3, 4, 5, Instant.now().plusSeconds(7200));
        taskPersistenceService.createTask(task1);
        taskPersistenceService.createTask(task2);

        // Act
        List<TaskEntity> tasks = taskPersistenceService.getAllTasks();

        // should we be assertting the whole task class, i.e. all fields
        // Assert
        assertTrue(tasks.size() >= 2); // at least the two we added
        assertTrue(tasks.stream().anyMatch(t -> t.getDescription().equals("Task 1")));
        assertTrue(tasks.stream().anyMatch(t -> t.getDescription().equals("Task 2")));
    }

    // TODO test uipdate description


    @Test
    void updateTaskScore_ShouldUpdateInDatabase() {
        // Arrange
        TaskEntity taskEntity = new TaskEntity("Update Score Test", 1, 1, 1, Instant.now().plusSeconds(3600));
        Long taskId = taskPersistenceService.createTask(taskEntity);

        // Act
        taskPersistenceService.updateTaskScore(taskId, ScoreType.EFFORT, 8);

        // Assert
        TaskEntity updatedTask = taskRepository.fetchTaskById(taskId);
        assertEquals(8, updatedTask.getEffort());
        assertEquals(1, updatedTask.getImpact()); // unchanged
        assertEquals(1, updatedTask.getUrgency()); // unchanged
    }

    // TODO test update deadline

}
