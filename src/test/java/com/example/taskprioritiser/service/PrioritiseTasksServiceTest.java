package com.example.taskprioritiser.service;

import com.example.taskprioritiser.service.model.Task;
import com.example.taskprioritiser.service.model.TaskPriority;
import com.example.taskprioritiser.service.prioritserFeature.PrioritiseTasksService;
import com.example.taskprioritiser.service.prioritserFeature.PriorityScoringService;
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
class PrioritiseTasksServiceTest {

    @Mock
    private PriorityScoringService priorityScoringService;


    @InjectMocks
    private PrioritiseTasksService underTest;


// TODO why is this mock check failing?
//    @Test
//    void getPrioritisedTasks_ShouldGetTaskPriorityForEach() {
//        List<Task> tasks = TestHelper.getDefaultTasks();
//        when(priorityScoringService.getTaskPriority(isA(Task.class), isA(LocalDateTime.class))).thenReturn(new TaskPriority(tasks.get(0), false, 2));
//
//        underTest.prioritiseTasks(tasks);
//        verify(priorityScoringService.getTaskPriority(isA(Task.class), isA(LocalDateTime.class)), times(tasks.size()));
//    }

    @Test
    void prioritiseTasks_ShouldReturnTasksInOrderOfPriority() {
        // With
        TaskPriority taskPriority1 = TestTaskPriorityBuilder.create().withTaskId(1L).withPriorityScore(25).build();
        TaskPriority taskPriority2 = TestTaskPriorityBuilder.create().withTaskId(2L).withPriorityScore(18.5).build();
        TaskPriority taskPriority3 = TestTaskPriorityBuilder.create().withTaskId(3L).withPriorityScore(44).build();
        TaskPriority taskPriority4 = TestTaskPriorityBuilder.create().withTaskId(4L).withPriorityScore(0.7).build();

        when(priorityScoringService.getTaskPriority(isA(Task.class), isA(LocalDateTime.class)))
                .thenReturn(taskPriority1)
                .thenReturn(taskPriority2).
                thenReturn(taskPriority3)
                .thenReturn(taskPriority4);

        // When
        List<Task> tasks = Stream.of(taskPriority1, taskPriority2, taskPriority3, taskPriority4).map(TaskPriority::getTask).toList();
        List<Task> prioritisedTasks = underTest.prioritiseTasks(tasks);

        //Then
        assertThat(prioritisedTasks).hasSize(4).extracting(Task::getTaskId).containsExactly(3L, 1L, 2L, 4L);
    }

    @Test
    void prioritiseTasks_ShouldReturnTasksInPriorityOrder_WithTasksDueTodayFirst() {
        // With
        TaskPriority taskPriority1 = TestTaskPriorityBuilder.create().withTaskId(1L).withPriorityScore(25).build();
        TaskPriority taskPriority2 = TestTaskPriorityBuilder.create().withTaskId(2L).withIsDueToday().withPriorityScore(18.5).build();
        TaskPriority taskPriority3 = TestTaskPriorityBuilder.create().withTaskId(3L).build();
        TaskPriority taskPriority4 = TestTaskPriorityBuilder.create().withTaskId(4L).withIsDueToday().build();

        // does that work?
        when(priorityScoringService.getTaskPriority(isA(Task.class), isA(LocalDateTime.class)))
                .thenReturn(taskPriority1)
                .thenReturn(taskPriority2).
                thenReturn(taskPriority3)
                .thenReturn(taskPriority4);
        // When
        List<Task> tasks = Stream.of(taskPriority1, taskPriority2, taskPriority3, taskPriority4).map(TaskPriority::getTask).toList();
        List<Task> prioritisedTasks = underTest.prioritiseTasks(tasks);

        //Then
        assertThat(prioritisedTasks).hasSize(4).extracting(Task::getTaskId).containsExactly(2L, 4L, 1L, 3L);
    }

}
