package com.example.taskprioritiser.service;

import com.example.taskprioritiser.PrioritiserConfig;
import com.example.taskprioritiser.ServiceConfig;
import com.example.taskprioritiser.internal.service.PriorityScoringService;
import com.example.taskprioritiser.internal.service.TaskPrioritisationService;
import com.example.taskprioritiser.internal.service.TaskService;
import com.example.taskprioritiser.internal.service.model.*;
import org.assertj.core.util.TriFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskPrioritisationServiceTest {

    @Mock
    private TaskService taskService;

    // TODO inject clock

    @Mock
    private PriorityScoringService priorityScoringService;

    @Mock(lenient = true)
    private ServiceConfig serviceConfig;

    @InjectMocks
    private TaskPrioritisationService taskPrioritisationService;

    // TODO fix this
    private final ZoneId zoneId = ZoneId.of("UTC");

    @BeforeEach
    void setUp() {
        // TODO fix this
        when(serviceConfig.getZoneId()).thenReturn(zoneId);
    }

    @Test
    void getPrioritisedTasks_ShouldReturnTasksPrioritisedByScore() {
        // rogue other scores can happen
    }

    @Test
    void getPrioritisedTasks_ShouldReturnTasksPrioritisedByScore_WithNoneDueToday() {

        //With these tasks
        Task task1 = createTaskWithDeadline(1L, Instant::minus, 1, ChronoUnit.DAYS)); // 44
        Task task2 = createTaskWithDeadline(2L, Instant::minus, 4, ChronoUnit.DAYS)); // 21.5
        Task task3 = createTaskWithDeadline(3L, Instant::minus, 7, ChronoUnit.DAYS)); // 18.29
        Task task4 = createTaskWithDeadline(4L, Instant::plus, 1, ChronoUnit.DAYS)); // 19
        Task task5 = createTaskWithDeadline(5L, Instant::plus, 4, ChronoUnit.DAYS)); // 16.5
        Task task6 = createTaskWithDeadline(6L, Instant::plus, 7, ChronoUnit.DAYS)); // 15.66
        List<Task> tasks = List.of(task1, task2, task3, task4, task5, task6);
        when(taskService.getAllTasks()).thenReturn(tasks);

        // When
        List<Task> prioritisedTasks = taskPrioritisationService.getPrioritisedTasks();

        // Then
        assertThat(prioritisedTasks).hasSize(6)
                .extracting(Task::getTaskId)
                .containsExactly(1L, 2L, 4L, 3L, 5L, 6L);
    }

    //test for the splitting of today
    @Test
    void getPrioritisedTasks_ShouldReturnTasksPrioritisedByDueTodayThenScore() {
        //With these tasks
        Task task1 = createTaskWithDeadline(1L, Instant::minus, 1, ChronoUnit.HOURS); // 29
        Task task2 = createTaskWithDeadline(2L, Instant::minus, 4, ChronoUnit.DAYS); // 21.5
        Task task3 = createTaskWithDeadline(3L, Instant::minus, 7, ChronoUnit.DAYS); // 18.29
        // TODO how? 4 should be bigger than 5?
        Task task4 = createTaskWithDeadline(4L, Instant::plus,2, ChronoUnit.HOURS); // 19
        Task task5 = createTaskWithDeadline(5L, Instant::plus, 4, ChronoUnit.HOURS); // 21.5
        Task task6 = createTaskWithDeadline(6L, Instant::plus, 7, ChronoUnit.DAYS); // 15.66
        List<Task> tasks = List.of(task1, task2, task3, task4, task5, task6);
        when(taskService.getAllTasks()).thenReturn(tasks);

        // When
        List<Task> prioritisedTasks = taskPrioritisationService.getPrioritisedTasks();

        // Then
        assertThat(prioritisedTasks).hasSize(6)
                .extracting(Task::getTaskId)
                .containsExactly(1L, 5L, 4L, 2L, 3L, 6L);
    }


    private Task createTaskWithIdAndDeadline(Long id, Instant deadline) {
        return TestTaskBuilder.create().withTaskId(id).withDeadline(deadline).build();
    }

    private Task createTaskWithDeadline(Long id, TriFunction<Instant, Long, TemporalUnit, Instant> operator, long amount, TemporalUnit unit) {
        Instant deadline = TestTimeHelper.createTime(operator, amount, unit);
        Task task = TestTaskBuilder.create().withDeadline(deadline).build();
        when(priorityScoringService.calculatePriorityScore(task, false)).thenReturn(priorityScore);
        return TestTaskBuilder.create().withDeadline(deadline).build();
    }
}
