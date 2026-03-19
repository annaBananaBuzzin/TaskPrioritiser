package com.example.taskprioritiser.repsoitory;

import com.example.taskprioritiser.service.ScoreType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskPersistenceServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskPersistenceService taskPersistenceService;

    // TODO - change how task entity test classes are created
    // TODO - do we wanna test the entity stuff?
    private TaskEntity taskEntity;
    private Long taskId = 1L;
    private String description = "Test Task";
    private int effort = 5;
    private int impact = 4;
    private int urgency = 3;
    private Instant deadline = Instant.now().plusSeconds(3600);

    @BeforeEach
    void setUp() {
        taskEntity = new TaskEntity(taskId, description, effort, impact, urgency, deadline);
    }

    @Test
    void createTask_ShouldReturnTaskId_WhenValid() {
        // Arrange
        when(taskRepository.fetchTaskByDescription(description)).thenReturn(null);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        // Act
        Long result = taskPersistenceService.createTask(taskEntity);

        // Assert
        assertEquals(taskId, result);
        verify(taskRepository).fetchTaskByDescription(description);
        verify(taskRepository).save(taskEntity);
    }

    @Test
    void createTask_ShouldThrowException_WhenDescriptionNotUnique() {
        // Arrange
        when(taskRepository.fetchTaskByDescription(description)).thenReturn(taskEntity);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskPersistenceService.createTask(taskEntity));
        assertEquals("Description must be unique", exception.getMessage());
        verify(taskRepository).fetchTaskByDescription(description);
//        verify(taskRepository, never()).save(anyString(), anyInt(), anyInt(), anyInt(), any(Instant.class));
    }

    @Test
    void updateTaskDescription_ShouldCallRepository() {
        // Act
        taskPersistenceService.updateTaskDescription(taskId, description);

        // Assert
        verify(taskRepository).updateDescription(taskId, description);
    }

    // TODO - won\t update task if description not unique

    @Test
    void updateTaskScore_ShouldCallUpdateEffort_WhenEffort() {
        // Act
        taskPersistenceService.updateTaskScore(taskId, ScoreType.EFFORT, effort);

        // Assert
        verify(taskRepository).updateEffortScore(taskId, effort);
        verify(taskRepository, never()).updateImpactScore(anyLong(), anyInt());
        verify(taskRepository, never()).updateUrgencyScore(anyLong(), anyInt());
    }

    @Test
    void updateTaskScore_ShouldCallUpdateImpact_WhenImpact() {
        // Act
        taskPersistenceService.updateTaskScore(taskId, ScoreType.IMPACT, impact);

        // Assert
        verify(taskRepository).updateImpactScore(taskId, impact);
        verify(taskRepository, never()).updateEffortScore(anyLong(), anyInt());
        verify(taskRepository, never()).updateUrgencyScore(anyLong(), anyInt());
    }

    @Test
    void updateTaskScore_ShouldCallUpdateUrgency_WhenUrgency() {
        // Act
        taskPersistenceService.updateTaskScore(taskId, ScoreType.URGENCY, urgency);

        // Assert
        verify(taskRepository).updateUrgencyScore(taskId, urgency);
        verify(taskRepository, never()).updateEffortScore(anyLong(), anyInt());
        verify(taskRepository, never()).updateImpactScore(anyLong(), anyInt());
    }

    @Test
    void updateTaskDeadline_ShouldCallRepository() {
        // Act
        taskPersistenceService.updateTaskDeadline(taskId, deadline);

        // Assert
        verify(taskRepository).updateDeadline(taskId, deadline);
    }

    // TODO add miltuple entities
    @Test
    void getAllTasks_ShouldReturnList() {
        // Arrange
        List<TaskEntity> tasks = List.of(taskEntity);
        when(taskRepository.fetchAllTasks()).thenReturn(tasks);

        // Act
        List<TaskEntity> result = taskPersistenceService.getAllTasks();

        // Assert
        assertEquals(tasks, result);
        verify(taskRepository).fetchAllTasks();
    }

    @Test
    void getTask_ShouldReturnOptional_WhenTaskExists() {
        // Arrange
        when(taskRepository.fetchTaskById(taskId)).thenReturn(taskEntity);

        // Act
        Optional<TaskEntity> result = taskPersistenceService.getTask(taskId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(taskEntity, result.get());
        verify(taskRepository).fetchTaskById(taskId);
    }

    @Test
    void getTask_ShouldReturnEmptyOptional_WhenTaskNotExists() {
        // Arrange
        when(taskRepository.fetchTaskById(taskId)).thenReturn(null);

        // Act
        Optional<TaskEntity> result = taskPersistenceService.getTask(taskId);

        // Assert
        assertFalse(result.isPresent());
        verify(taskRepository).fetchTaskById(taskId);
    }
}