package com.example.taskprioritiser.service;

import com.example.taskprioritiser.TestHelper;
import com.example.taskprioritiser.service.model.Task;
import com.example.taskprioritiser.service.model.TaskPriority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskPrioritisationServiceTest {

    @Mock
    private TaskService taskService;

    @Mock
    private PriorityScoringService priorityScoringService;


    @InjectMocks
    private TaskPrioritisationService taskPrioritisationService;


    @Test
    void getPrioritisedTasks_ShouldGetAllTasks() {
        when(taskService.getAllTasks()).thenReturn(TestHelper.getDefaultTasks());
        verify(taskService.getAllTasks());
    }

    @Test
    void getPrioritisedTasks_ShouldGetTaskPriorityForEach() {
        List<Task> tasks = TestHelper.getDefaultTasks();
        when(taskService.getAllTasks()).thenReturn(tasks);

        when(priorityScoringService.getTaskPriority(isA(Task.class), isA(LocalDateTime.class))).thenReturn(new TaskPriority(tasks.get(0), false, 2));

        verify(priorityScoringService.getTaskPriority(isA(Task.class), isA(LocalDateTime.class)), times(tasks.size()));
    }

    @Test
    void getPrioritisedTasks_ShouldReturnTasksInOrderOfPriority() {
        // With
        TaskPriority taskPriority1 = TestTaskPriorityBuilder.create().withTaskId(1L).withPriorityScore(25).build();
        TaskPriority taskPriority2 = TestTaskPriorityBuilder.create().withTaskId(2L).withPriorityScore(18.5).build();
        TaskPriority taskPriority3 = TestTaskPriorityBuilder.create().withTaskId(3L).withPriorityScore(44).build();
        TaskPriority taskPriority4 = TestTaskPriorityBuilder.create().withTaskId(4L).withPriorityScore(0.7).build();

        when(taskService.getAllTasks()).thenReturn(List.of(taskPriority1.getTask(), taskPriority2.getTask(), taskPriority3.getTask(), taskPriority4.getTask()));

        // does that work?
        when(priorityScoringService.getTaskPriority(isA(Task.class), isA(LocalDateTime.class)))
                .thenReturn(taskPriority1)
                .thenReturn(taskPriority2).
                thenReturn(taskPriority3)
                .thenReturn(taskPriority4);

        List<Task> tasks = Stream.of(taskPriority1, taskPriority2, taskPriority3, taskPriority4).map(TaskPriority::getTask).toList();
        when(taskService.getAllTasks()).thenReturn(tasks);

        // When
        List<Task> prioritisedTasks = taskPrioritisationService.getPrioritisedTasks();

        //Then
        assertThat(prioritisedTasks).hasSize(4).extracting(Task::getTaskId).containsExactly(3L, 1L, 2L, 4L);
    }

    @Test
    void getPrioritisedTasks_ShouldReturnTasksInPriorityOrder_WithTasksDueTodayFirst() {
        // With
        TaskPriority taskPriority1 = TestTaskPriorityBuilder.create().withTaskId(1L).withPriorityScore(25).build();
        TaskPriority taskPriority2 = TestTaskPriorityBuilder.create().withTaskId(2L).withIsDueToday().withPriorityScore(18.5).build();
        TaskPriority taskPriority3 = TestTaskPriorityBuilder.create().withTaskId(3L).build();
        TaskPriority taskPriority4 = TestTaskPriorityBuilder.create().withTaskId(4L).withIsDueToday().build();

        when(taskService.getAllTasks()).thenReturn(List.of(taskPriority1.getTask(), taskPriority2.getTask(), taskPriority3.getTask(), taskPriority4.getTask()));

        // does that work?
        when(priorityScoringService.getTaskPriority(isA(Task.class), isA(LocalDateTime.class)))
                .thenReturn(taskPriority1)
                .thenReturn(taskPriority2).
                thenReturn(taskPriority3)
                .thenReturn(taskPriority4);

        List<Task> tasks = Stream.of(taskPriority1, taskPriority2, taskPriority3, taskPriority4).map(TaskPriority::getTask).toList();
        when(taskService.getAllTasks()).thenReturn(tasks);

        // When
        List<Task> prioritisedTasks = taskPrioritisationService.getPrioritisedTasks();

        //Then
        assertThat(prioritisedTasks).hasSize(4).extracting(Task::getTaskId).containsExactly(2L, 4L, 3L, 1L);
    }

}
