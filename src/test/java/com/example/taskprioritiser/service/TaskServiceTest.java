package com.example.taskprioritiser.service;

import com.example.taskprioritiser.internal.service.repsoitory.entity.TaskEntity;
import com.example.taskprioritiser.internal.service.repsoitory.TaskPersistenceService;
import com.example.taskprioritiser.internal.service.model.*;
import com.example.taskprioritiser.internal.service.TaskService;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskPersistenceService taskPersistenceService;

    @InjectMocks
    private TaskService taskService;

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
        Task result = taskService.createTask(newTask);

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
            () -> taskService.createTask(newTask));
        assertEquals("Description cannot be null or empty", exception.getMessage());

        verify(taskPersistenceService, never());
    }

    // TODO should be able to cycle through the enum values

    @ParameterizedTest
    @ValueSource (ints = {-4, 0, 11})
    void createTask_ShouldThrowException_WhenEffortScoreInvalid(int value) {
        // When
        NewTask newTask = createNewTaskWithScoreValues(value, impactValue, urgencyValue);

        // Then
        assertCreateTaskThrowsWithInvalidScoreValue(newTask, ScoreType.EFFORT);
    }

    @ParameterizedTest
    @ValueSource (ints = {-4, 0, 11})
    void createTask_ShouldThrowException_WhenImpactScoreInvalid(int value) {
        // When
        NewTask newTask = createNewTaskWithScoreValues(effortValue, value, urgencyValue);

        // Then
        assertCreateTaskThrowsWithInvalidScoreValue(newTask, ScoreType.IMPACT);
    }


    @ParameterizedTest
    @ValueSource (ints = {-4, 0, 11})
    void createTask_ShouldThrowException_WhenUrgencyScoreInvalid(int value) {
        // When
        NewTask newTask = createNewTaskWithScoreValues(effortValue, impactValue, value);

        // Then
        assertCreateTaskThrowsWithInvalidScoreValue(newTask, ScoreType.URGENCY);
    }

    @Test
    void createTask_ShouldThrowException_WhenDeadlineInPast() {
        // With
        Instant pastDeadline = Instant.now().minusSeconds(3600);

        // When
        NewTask newTask = createNewTaskWithDeadline(pastDeadline);

        // Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskService.createTask(newTask));
        assertEquals("Deadline must be in the future", exception.getMessage());

        verify(taskPersistenceService, never());
    }

    @Test
    void createTask_ShouldThrowException_WhenTaskNotRetrievedAfterCreation() {
        // With
        when(taskPersistenceService.createTask(any())).thenReturn(taskId);
        when(taskPersistenceService.getTask(taskId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> taskService.createTask(any()));
        assertEquals("Failed to retrieve the newly created task with ID: " + taskId, exception.getMessage());
    }

    @Test
    void updateTaskDescription_ShouldCallPersistenceService_WhenValid() {
        // When
        String newDescription = "Updated Description";
        taskService.updateTaskDescription(taskId, newDescription);

        // Then
        verify(taskPersistenceService).updateTaskDescription(taskId, newDescription);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void updateTaskDescription_ShouldThrowException_WhenDescriptionInvalid(String description) {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskService.updateTaskDescription(taskId, description));
        assertEquals("Description cannot be null or empty", exception.getMessage());
        verify(taskPersistenceService, never()).updateTaskDescription(anyLong(), anyString());
    }

    @ParameterizedTest
    @EnumSource (ScoreType.class)
    void updateTaskScore_WithScoreType_ShouldCallUpdateTaskScore(ScoreType scoreType) {
        // With
        int value = 7;

        // When
        taskService.updateTaskScore(taskId, scoreType, value);

        // Then
        verify(taskPersistenceService).updateTaskScore(eq(taskId), eq(scoreType), eq(value));
    }

    @ParameterizedTest
    @ValueSource (ints = {-4, 0, 11})
    void updateTaskScore_ShouldThrowException_WhenScoreInvalid(int value) {
        Stream.of(ScoreType.values())
                .forEach(type ->
                        assertUpdateTaskThrowsWithInvalidScoreValue(type, value));
    }

    @Test
    void updateTaskDeadline_ShouldCallPersistenceService_WhenValid() {
        // When
        taskService.updateTaskDeadline(taskId, deadline);

        // Then
        verify(taskPersistenceService).updateTaskDeadline(taskId, deadline);
    }

    @Test
    void updateTaskDeadline_ShouldThrowException_WhenDeadlineInPast() {
        // With
        Instant pastDeadline = Instant.now().minusSeconds(3600);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskService.updateTaskDeadline(taskId, pastDeadline));
        assertEquals("Deadline must be in the future", exception.getMessage());
        verify(taskPersistenceService, never()).updateTaskDeadline(anyLong(), any(Instant.class));
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() {
        // With
        TaskEntity entity1 = createTask(taskId);
        TaskEntity entity2 = createTask(2L);
        TaskEntity entity3 = createTask(3L);
        TaskEntity entity4 = createTask(4L);
        List<TaskEntity> entities = List.of(entity1, entity2, entity3, entity4);
        when(taskPersistenceService.getAllTasks()).thenReturn(entities);

        // When
        List<Task> result = taskService.getAllTasks();

        // Then
        assertThat(result).hasSize(4)
                .extracting(Task::getTaskId)
                .containsAll(List.of(taskId, 2L, 3L, 4L));
        verify(taskPersistenceService).getAllTasks();
    }

    @Test
    void getAllTasks_ShouldReturnEmptyList_WhenNoTasks() {
        // With
        when(taskPersistenceService.getAllTasks()).thenReturn(List.of());

        // When
        List<Task> result = taskService.getAllTasks();

        // Then
        assertTrue(result.isEmpty());
        verify(taskPersistenceService).getAllTasks();
    }

    @Test
    void getTask_ShouldReturnTask_WhenExists() {
        // With
        when(taskPersistenceService.getTask(taskId)).thenReturn(Optional.of(createTask(taskId)));

        // When
        Task result = taskService.getTask(taskId);

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
            () -> taskService.getTask(taskId));
        assertEquals("Task not found with ID: " + taskId, exception.getMessage());
    }

    void assertCreateTaskThrowsWithInvalidScoreValue(NewTask newTask, ScoreType scoreType){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(newTask));
        assertEquals(scoreType + " score must be between 1 and 10", exception.getMessage());

        verify(taskPersistenceService, never());
    }

    void assertUpdateTaskThrowsWithInvalidScoreValue(ScoreType scoreType, int value){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.updateTaskScore(taskId, scoreType, value));
        assertEquals(scoreType + " score must be between 1 and 10", exception.getMessage());

        verify(taskPersistenceService, never());
    }

    // Class creator helper methods
    private TaskEntity createTask(Long taskId){
        return new TaskEntity(taskId, description, effortValue, impactValue, urgencyValue, deadline);
    }

    private NewTask createNewTask(){
        return createNewTask(description, effortValue, impactValue, urgencyValue);    }

    private NewTask createNewTaskWithDescription(String description){
        return createNewTask(description, effortValue, impactValue, urgencyValue);    }

    private NewTask createNewTaskWithScoreValues(int effortValue, int impactValue, int urgencyValue){
        return createNewTask(description, effortValue, impactValue, urgencyValue);
    }

    private NewTask createNewTaskWithDeadline(Instant deadline){
        return createNewTask(description, effortValue, impactValue, urgencyValue, deadline);
    }

    private NewTask createNewTask(String description, int effortValue, int impactValue, int urgencyValue){
        return createNewTask(description, effortValue, impactValue, urgencyValue, deadline);
    }

    private NewTask createNewTask(String description, int effortValue, int impactValue, int urgencyValue, Instant deadline){
        return new NewTask(description, new EffortScore(effortValue), new ImpactScore(impactValue), new UrgencyScore(urgencyValue), deadline);
    }
}