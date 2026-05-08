package com.example.taskprioritiser.service.task;

import com.example.taskprioritiser.repsoitory.DoneTaskPersistenceService;
import com.example.taskprioritiser.repsoitory.DoneTaskProjection;
import com.example.taskprioritiser.repsoitory.TaskEntity;
import com.example.taskprioritiser.repsoitory.TaskPersistenceService;
import com.example.taskprioritiser.service.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TasksManagementServiceTest {

    @Mock
    private TaskPersistenceService taskPersistenceService;
        @Mock
    private DoneTaskPersistenceService doneTaskPersistenceService;

    @InjectMocks
    private TasksManagementService underTest;


    @Test
    void markTaskAsDone_ShouldMarkTaskAsDone_WhenTaskExists() {
        // With
        Long taskId = 1L;
        when(taskPersistenceService.getTask(taskId)).thenReturn(java.util.Optional.of(createTask(taskId)));

        // When
        underTest.markTaskAsDone(taskId);

        // Then
        verify(taskPersistenceService).getTask(taskId);
        verify(doneTaskPersistenceService).saveDoneTask(taskId);
    }

    @Test
    void markTaskAsDone_ShouldThrowException_WhenTaskDoesNotExist() {
        // With
        Long taskId = 1L;
        when(taskPersistenceService.getTask(taskId)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> underTest.markTaskAsDone(taskId));
        verify(taskPersistenceService).getTask(taskId);
        verify(doneTaskPersistenceService, never()).saveDoneTask(taskId);
    }

    @Test
    void unmarkTaskAsDone_ShouldUnmarkTaskAsDone_WhenTaskExists() {
        // With
        Long taskId = 1L;
        when(taskPersistenceService.getTask(taskId)).thenReturn(java.util.Optional.of(createTask(taskId)));

        // When
        underTest.unmarkTaskAsDone(taskId);

        // Then
        verify(taskPersistenceService).getTask(taskId);
        verify(doneTaskPersistenceService).deleteDoneTask(taskId);
    }

    @Test
    void unmarkTaskAsDone_ShouldThrowException_WhenTaskDoesNotExist() {
        // With
        Long taskId = 1L;
        when(taskPersistenceService.getTask(taskId)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> underTest.unmarkTaskAsDone(taskId));
        verify(taskPersistenceService).getTask(taskId);
        verify(doneTaskPersistenceService, never()).deleteDoneTask(taskId);
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() {
        // With
        TaskEntity entity1 = createTask(1L);
        TaskEntity entity2 = createTask(2L);
        TaskEntity entity3 = createTask(3L);
        TaskEntity entity4 = createTask(4L);
        List<TaskEntity> entities = List.of(entity1, entity2, entity3, entity4);
        when(taskPersistenceService.getAllTasks()).thenReturn(entities);

        // When
        List<Task> result = underTest.getAllTasks();

        // Then
        assertThat(result).hasSize(4)
                .extracting(Task::getTaskId)
                .containsAll(List.of(1L, 2L, 3L, 4L));
        verify(taskPersistenceService).getAllTasks();
    }

    @Test
    void getAllTasks_ShouldReturnEmptyList_WhenNoTasks() {
        // With
        when(taskPersistenceService.getAllTasks()).thenReturn(List.of());

        // When
        List<Task> result = underTest.getAllTasks();

        // Then
        assertTrue(result.isEmpty());
        verify(taskPersistenceService).getAllTasks();
    }

        @Test
    void getAllOutstandingTasks_ShouldReturnListOfTasks() {
        // With
        TaskEntity entity1 = createTask(1L);
        TaskEntity entity2 = createTask(2L);
        TaskEntity entity3 = createTask(3L);
        TaskEntity entity4 = createTask(4L);
        List<TaskEntity> entities = List.of(entity1, entity2, entity3, entity4);
        when(doneTaskPersistenceService.fetchAllNotDoneTasks()).thenReturn(entities);

        // When
        List<Task> result = underTest.getAllOutstandingTasks();

        // Then
        assertThat(result).hasSize(4)
                .extracting(Task::getTaskId)
                .containsAll(List.of(1L, 2L, 3L, 4L));
        verify(doneTaskPersistenceService).fetchAllNotDoneTasks();
    }

    @Test
    void getAllOutstandingTasks_ShouldReturnEmptyList_WhenNoTasks() {
        // With
        when(doneTaskPersistenceService.fetchAllNotDoneTasks()).thenReturn(List.of());

        // When
        List<Task> result = underTest.getAllOutstandingTasks();

        // Then
        assertTrue(result.isEmpty());
        verify(doneTaskPersistenceService).fetchAllNotDoneTasks();
    }

    // can fetch list of and filter to only show tasks due today
    // can fetch and filter out all tasks if none due today

    private TaskEntity createTask(Long taskId) {
        return new TaskEntity(taskId, "Task description", 5, 4, 2, Instant.now().plusSeconds(3600));
    }
}