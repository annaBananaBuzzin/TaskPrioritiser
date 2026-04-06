package com.example.taskprioritiser.repsoitory;

import com.example.taskprioritiser.internal.service.repsoitory.entity.TaskEntity;
import com.example.taskprioritiser.internal.service.repsoitory.TaskPersistenceService;
import com.example.taskprioritiser.internal.service.repsoitory.TaskRepository;
import com.example.taskprioritiser.internal.service.model.ScoreType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TaskPersistenceServiceIntegrationTest {

    @Autowired
    private TaskPersistenceService taskPersistenceService;

    @Autowired
    private TaskRepository taskRepository;

    private String description1;

    @BeforeEach
    void setUp(){
        description1 = "First Task";
        TaskEntity taskEntity1 = createTaskEntity(description1);
        TaskEntity taskEntity2 = createTaskEntity("Second Task");

        taskRepository.save(taskEntity1);
        taskRepository.save(taskEntity2);
    }

    @Test
    void createTask_ShouldInsertIntoDatabase_OnlyWithValidDescription() {
        TaskEntity invalidTaskEntity = createTaskEntity(description1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskPersistenceService.createTask(invalidTaskEntity));
        assertEquals("Description must be unique", exception.getMessage());

        String newTaskDescription = "New Task";
        TaskEntity validTaskEntity = createTaskEntity(newTaskDescription);
        Long taskId = taskPersistenceService.createTask(validTaskEntity);
        assertNotNull(taskId);

        Optional<TaskEntity> savedTask = taskPersistenceService.getTask(taskId);
        assertThat(savedTask).hasValueSatisfying(task -> {
            assertEquals(validTaskEntity.getDescription(), task.getDescription());
            assertEquals(validTaskEntity.getEffort(), task.getEffort());
            assertEquals(validTaskEntity.getImpact(), task.getImpact());
            assertEquals(validTaskEntity.getUrgency(), task.getUrgency());
            assertEquals(validTaskEntity.getDeadline(), task.getDeadline());
        });
    }

    @Test
    void updateTaskDescription_ShouldUpdateDatabase(){
        TaskEntity taskEntity = taskRepository.fetchTaskByDescription(description1);
        Long taskId = taskEntity.getTaskId();

        String newDescription = "Task number 1";
        taskPersistenceService.updateTaskDescription(taskId, newDescription);

        Optional<TaskEntity> updatedTask = taskPersistenceService.getTask(taskId);

        assertThat(updatedTask).hasValueSatisfying(task -> {
            assertEquals(newDescription, task.getDescription());
            assertEquals(taskEntity.getEffort(), task.getEffort());
            assertEquals(taskEntity.getImpact(), task.getImpact());
            assertEquals(taskEntity.getUrgency(), task.getUrgency());
            assertEquals(taskEntity.getDeadline(), task.getDeadline());
        });
    }

    @Test
    void updateTaskDescription_ShouldThrowException_WhenDescriptionNotUnique(){
        String newTaskDescription = "New Task";
        TaskEntity validTaskEntity = createTaskEntity(newTaskDescription);
        Long newTaskId = taskPersistenceService.createTask(validTaskEntity);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskPersistenceService.updateTaskDescription(newTaskId, description1));
        assertEquals("Description must be unique", exception.getMessage());
    }

    @Test
    void updateTaskScore_ShouldUpdateInDatabase() {
        TaskEntity savedTaskEntity = taskRepository.fetchTaskByDescription(description1);
        Long savedTaskId = savedTaskEntity.getTaskId();

        taskPersistenceService.updateTaskScore(savedTaskEntity.getTaskId(), ScoreType.EFFORT, 8);
        Optional<TaskEntity> updatedTask = taskPersistenceService.getTask(savedTaskId);
        assertThat(updatedTask).hasValueSatisfying(task -> {
            assertEquals(8, task.getEffort());
            assertNotEquals(savedTaskEntity.getEffort(), task.getEffort());
        });

        taskPersistenceService.updateTaskScore(savedTaskEntity.getTaskId(), ScoreType.IMPACT, 2);
        updatedTask = taskPersistenceService.getTask(savedTaskId);
        assertThat(updatedTask).hasValueSatisfying(task -> {
            assertEquals(2, task.getImpact());
            assertNotEquals(savedTaskEntity.getImpact(), task.getImpact());
        });

        taskPersistenceService.updateTaskScore(savedTaskEntity.getTaskId(), ScoreType.URGENCY, 9);
        updatedTask = taskPersistenceService.getTask(savedTaskId);
        assertThat(updatedTask).hasValueSatisfying(task -> {
            assertEquals(9, task.getUrgency());
            assertNotEquals(savedTaskEntity.getUrgency(), task.getUrgency());
        });
    }

    @Test
    void updateTaskDeadline_ShouldUpdateDatabase(){
        TaskEntity savedTaskEntity = taskRepository.fetchTaskByDescription(description1);
        Long savedTaskId = savedTaskEntity.getTaskId();

        Instant newDeadline = Instant.now().plusSeconds(8000);
        taskPersistenceService.updateTaskDeadline(savedTaskEntity.getTaskId(), newDeadline);

        Optional<TaskEntity> updatedTask = taskPersistenceService.getTask(savedTaskId);
        assertThat(updatedTask).hasValueSatisfying(task -> {
            assertEquals(newDeadline, task.getDeadline());
            assertNotEquals(savedTaskEntity.getDeadline(), task.getDeadline());
        });
    }

    @Test
    void getAllTasks_ShouldReturnFromDatabase() {
        List<TaskEntity> tasks = taskPersistenceService.getAllTasks();
        assertEquals(2, tasks.size());
    }

    private TaskEntity createTaskEntity(String description){
        return new TaskEntity(description, 3, 4, 6, Instant.now().plusSeconds(3600));
    }
}
