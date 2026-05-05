package com.example.taskprioritiser.repsoitory;

import com.example.taskprioritiser.service.model.ScoreType;
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

    private final Long taskId = 1L;
    private final String description = "First Task";

    @Test
    void createTask_ShouldReturnTaskId_WhenValid() {
        // With
        TaskEntity taskEntity = createTaskEntity();
        when(taskRepository.fetchTaskByDescription(description)).thenReturn(null);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        // When
        Long taskIdReturned = taskPersistenceService.createTask(taskEntity);

        // Assert
        assertEquals(taskId, taskIdReturned);
        verify(taskRepository).fetchTaskByDescription(description);
        verify(taskRepository).save(taskEntity);
    }

    @Test
    void createTask_ShouldThrowException_WhenDescriptionNotUnique() {
        // When
        TaskEntity taskEntity = createTaskEntity();
        when(taskRepository.fetchTaskByDescription(description)).thenReturn(taskEntity);

        // When & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskPersistenceService.createTask(taskEntity));
        assertEquals("Description must be unique", exception.getMessage());
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void updateTaskDescription_ShouldCheckUnique() {
        // With
        when(taskRepository.fetchTaskByDescription(description)).thenReturn(null);

        // When
        taskPersistenceService.updateTaskDescription(taskId, description);

        // Then
        verify(taskRepository).fetchTaskByDescription(description);
        verify(taskRepository).updateDescription(taskId, description);
    }

    @Test
    void updateTaskDescription_ShouldThrowException_WhenDescriptionNotUnique() {
        // With
        TaskEntity taskEntity = createTaskEntity();
        when(taskRepository.fetchTaskByDescription(description)).thenReturn(taskEntity);

        // When
        taskPersistenceService.updateTaskDescription(taskId, description);
        // When & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskPersistenceService.updateTaskDescription(taskId, description));
        assertEquals("Description must be unique", exception.getMessage());
        verify(taskRepository, never()).updateDescription(anyLong(), anyString());
    }

    @Test
    void updateTaskScore_ShouldCallUpdateEffort_WhenEffort() {
        // When
        taskPersistenceService.updateTaskScore(taskId, ScoreType.EFFORT, 5);

        // Then
        verify(taskRepository).updateEffortScore(taskId, 5);
        verify(taskRepository, never()).updateImpactScore(anyLong(), anyInt());
        verify(taskRepository, never()).updateUrgencyScore(anyLong(), anyInt());
    }

    @Test
    void updateTaskScore_ShouldCallUpdateImpact_WhenImpact() {
        // When
        taskPersistenceService.updateTaskScore(taskId, ScoreType.IMPACT, 3);

        // Then
        verify(taskRepository).updateImpactScore(taskId, 3);
        verify(taskRepository, never()).updateEffortScore(anyLong(), anyInt());
        verify(taskRepository, never()).updateUrgencyScore(anyLong(), anyInt());
    }

    @Test
    void updateTaskScore_ShouldCallUpdateUrgency_WhenUrgency() {
        // When
        taskPersistenceService.updateTaskScore(taskId, ScoreType.URGENCY, 3);

        // Then
        verify(taskRepository).updateUrgencyScore(taskId, 3);
        verify(taskRepository, never()).updateEffortScore(anyLong(), anyInt());
        verify(taskRepository, never()).updateImpactScore(anyLong(), anyInt());
    }

    @Test
    void updateTaskDeadline_ShouldCallRepository() {
        // When
        taskPersistenceService.updateTaskDeadline(taskId, Instant.now());

        // Then
        verify(taskRepository).updateDeadline(taskId, any(Instant.class));
    }

    @Test
    void deleteTask_ShouldCallRepository() {
        // When
        taskPersistenceService.deleteTask(taskId);

        // Then
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void getAllTasks_ShouldReturnList() {
        // With
        TaskEntity defaultTask = createTaskEntity();
        TaskEntity secondTask = createTaskEntity(2L, "Second Task");
        TaskEntity thirdTask = createTaskEntity(3L, "Third Task");
        List<TaskEntity> tasks = List.of(defaultTask, secondTask, thirdTask);
        when(taskRepository.fetchAllTasks()).thenReturn(tasks);

        // When
        List<TaskEntity> result = taskPersistenceService.getAllTasks();

        // Then
        assertEquals(tasks, result);
        verify(taskRepository).fetchAllTasks();
    }

    @Test
    void getAllTasks_ShouldReturnEmptyList_WhenNoTasks() {
        // With
        when(taskRepository.fetchAllTasks()).thenReturn(List.of());

        // When
        List<TaskEntity> result = taskPersistenceService.getAllTasks();

        // Then
        assertTrue(result.isEmpty());
        verify(taskRepository).fetchAllTasks();
    }

    @Test
    void getTask_ShouldReturnOptional_WhenTaskExists() {
        // With
        TaskEntity taskEntity = createTaskEntity();
        when(taskRepository.fetchTaskById(taskId)).thenReturn(taskEntity);

        // When
        Optional<TaskEntity> result = taskPersistenceService.getTask(taskId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(taskEntity, result.get());
        verify(taskRepository).fetchTaskById(taskId);
    }

    @Test
    void getTask_ShouldReturnEmptyOptional_WhenTaskNotExists() {
        // With
        when(taskRepository.fetchTaskById(taskId)).thenReturn(null);

        // When
        Optional<TaskEntity> result = taskPersistenceService.getTask(taskId);

        // Then
        assertFalse(result.isPresent());
        verify(taskRepository).fetchTaskById(taskId);
    }

    // Class creator helper methods
    private TaskEntity createTaskEntity(){
        return createTaskEntity(taskId, description);
    }

    private TaskEntity createTaskEntity(Long taskId, String description){
        return new TaskEntity(taskId, description, 3, 4, 6, Instant.now().plusSeconds(3600));
    }
}