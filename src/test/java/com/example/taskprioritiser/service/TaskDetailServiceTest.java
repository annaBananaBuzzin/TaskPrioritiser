package com.example.taskprioritiser.service;

import com.example.taskprioritiser.repsoitory.TaskEntity;
import com.example.taskprioritiser.repsoitory.TaskPersistenceService;
import com.example.taskprioritiser.service.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskDetailServiceTest {

    @Mock
    private TaskPersistenceService taskPersistenceService;

    @InjectMocks
    private TaskDetailService taskDetailService;

    private final Long taskId = 1L;
    private final String description = "First Task";
    private final int effortValue = 5;
    private final int impactValue = 4;
    private final int urgencyValue = 2;
    private final Instant deadline = Instant.now().plusSeconds(3600);

    @Test
    void createTask_ShouldReturnTask_WhenValid() {
        // With
        when(taskPersistenceService.createTask(any())).thenReturn(taskId);
        when(taskPersistenceService.getTask(taskId)).thenReturn(Optional.of(createTask(taskId)));
        NewTask newTask = createNewTask();

        // When
        Task result = taskDetailService.createTask(newTask);

        // Then
        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(taskId, result.getTaskId());
        verify(taskPersistenceService).createTask(any());
        verify(taskPersistenceService).getTask(taskId);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void createTask_ShouldThrowException_WhenDescriptionInvalid(String description) {
        // TODO check what happens with white spaces?
        // With
        NewTask newTask = createNewTaskWithDescription(description);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskDetailService.createTask(newTask));
        assertEquals("Description cannot be null or empty", exception.getMessage());

        verifyNoInteractions(taskPersistenceService);

    }


    @Test
    void createTask_ShouldThrowException_WhenDeadlineInPast() {
        // With
        Instant pastDeadline = Instant.now().minusSeconds(3600);

        // When
        NewTask newTask = createNewTaskWithDeadline(pastDeadline);

        // Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskDetailService.createTask(newTask));
        assertEquals("Deadline must be in the future", exception.getMessage());

        verifyNoInteractions(taskPersistenceService);
    }

    @Test
    void createTask_ShouldThrowException_WhenTaskNotRetrievedAfterCreation() {
        // With
        when(taskPersistenceService.createTask(any())).thenReturn(taskId);
        when(taskPersistenceService.getTask(taskId)).thenReturn(Optional.empty());
        NewTask newTask = createNewTask();

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taskDetailService.createTask(newTask));
        assertEquals("Failed to retrieve the newly created task with ID: " + taskId, exception.getMessage());
    }

    @Test
    void updateTaskDescription_ShouldCallPersistenceService_WhenValid() {
        // When
        String newDescription = "Updated Description";
        taskDetailService.updateTaskDescription(taskId, newDescription);

        // Then
        verify(taskPersistenceService).updateTaskDescription(taskId, newDescription);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void updateTaskDescription_ShouldThrowException_WhenDescriptionInvalid(String description) {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskDetailService.updateTaskDescription(taskId, description));
        assertEquals("Description cannot be null or empty", exception.getMessage());
        verify(taskPersistenceService, never()).updateTaskDescription(anyLong(), anyString());
    }

    @ParameterizedTest
    @EnumSource(ScoreType.class)
    void updateTaskScore_WithScoreType_ShouldCallUpdateTaskScore(ScoreType scoreType) {
        // With
        int value = 7;

        // When
        taskDetailService.updateTaskScore(taskId, scoreType, value);

        // Then
        verify(taskPersistenceService).updateTaskScore(eq(taskId), eq(scoreType), eq(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {-4, 0, 11})
    void updateTaskScore_ShouldThrowException_WhenScoreInvalid(int value) {
        Stream.of(ScoreType.values())
                .forEach(type ->
                        assertUpdateTaskThrowsWithInvalidScoreValue(type, value));
    }

    @Test
    void updateTaskDeadline_ShouldCallPersistenceService_WhenValid() {
        // When
        taskDetailService.updateTaskDeadline(taskId, deadline);

        // Then
        verify(taskPersistenceService).updateTaskDeadline(taskId, deadline);
    }

    @Test
    void updateTaskDeadline_ShouldThrowException_WhenDeadlineInPast() {
        // With
        Instant pastDeadline = Instant.now().minusSeconds(3600);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskDetailService.updateTaskDeadline(taskId, pastDeadline));
        assertEquals("Deadline must be in the future", exception.getMessage());
        verify(taskPersistenceService, never()).updateTaskDeadline(anyLong(), any(Instant.class));
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        // With
        when(taskPersistenceService.getTask(taskId)).thenReturn(Optional.of(createTask(taskId)));

        // When & Then
        taskDetailService.deleteTask(taskId);
        verify(taskPersistenceService).getTask(eq(taskId));
        verify(taskPersistenceService).deleteTask(eq(taskId));
    }

    @Test
    void deleteTask_ShouldCatchException_WhenTaskDoesNotExist() {
        // With
        when(taskPersistenceService.getTask(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertDoesNotThrow(() -> taskDetailService.deleteTask(taskId));
        verify(taskPersistenceService, never()).deleteTask(anyLong());
    }

    @Test
    void getTask_ShouldReturnTask_WhenExists() {
        // With
        when(taskPersistenceService.getTask(taskId)).thenReturn(Optional.of(createTask(taskId)));

        // When
        Task result = taskDetailService.getTask(taskId);

        // Then
        assertNotNull(result);
        assertEquals(description, result.getDescription());
        verify(taskPersistenceService).getTask(taskId);
    }

    @Test
    void getTask_ShouldThrowException_WhenNotExists() {
        // With
        when(taskPersistenceService.getTask(taskId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taskDetailService.getTask(taskId));
        assertEquals("Task not found with ID: " + taskId, exception.getMessage());
    }

    void assertCreateTaskThrowsWithInvalidScoreValue(NewTask newTask, ScoreType scoreType) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskDetailService.createTask(newTask));
        assertEquals(scoreType + " score value must be between 1 and 10", exception.getMessage());

        verifyNoInteractions(taskPersistenceService);
    }

    void assertUpdateTaskThrowsWithInvalidScoreValue(ScoreType scoreType, int value) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskDetailService.updateTaskScore(taskId, scoreType, value));
        assertEquals(scoreType + " score value must be between 1 and 10", exception.getMessage());

        verifyNoInteractions(taskPersistenceService);

    }

    // Class creator helper methods
    private TaskEntity createTask(Long taskId) {
        return new TaskEntity(taskId, description, effortValue, impactValue, urgencyValue, deadline);
    }

    private NewTask createNewTask() {
        return createNewTask(description, deadline);
    }

    private NewTask createNewTaskWithDescription(String description) {
        return createNewTask(description, deadline);
    }

    private NewTask createNewTaskWithDeadline(Instant deadline) {
        return createNewTask(description, deadline);
    }

    private NewTask createNewTask(String description, Instant deadline) {
        return new NewTask(description, new EffortScore(effortValue), new ImpactScore(impactValue), new UrgencyScore(urgencyValue), deadline);
    }
}