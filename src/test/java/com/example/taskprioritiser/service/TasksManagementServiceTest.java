package com.example.taskprioritiser.service;

import com.example.taskprioritiser.repsoitory.TaskEntity;
import com.example.taskprioritiser.repsoitory.TaskPersistenceService;
import com.example.taskprioritiser.service.model.Task;
import com.example.taskprioritiser.service.task.TasksManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TasksManagementServiceTest {

    @Mock
    private TaskPersistenceService taskPersistenceService;

    @InjectMocks
    private TasksManagementService underTest;


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

    private TaskEntity createTask(Long taskId) {
        return new TaskEntity(taskId, "Task description", 5, 4, 2, Instant.now().plusSeconds(3600));
    }
}