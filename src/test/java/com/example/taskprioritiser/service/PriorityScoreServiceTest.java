package com.example.taskprioritiser.service;

import com.example.taskprioritiser.PrioritiserConfig;
import com.example.taskprioritiser.ServiceConfig;
import com.example.taskprioritiser.internal.service.PriorityScoreService;
import com.example.taskprioritiser.internal.service.TaskService;
import com.example.taskprioritiser.internal.service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriorityScoreServiceTest {

    @Mock
    private TaskService taskService;

    @Mock(lenient = true)
    // does that really need to be mocked?
    private PrioritiserConfig prioritiserConfig;

    @Mock(lenient = true)
    private ServiceConfig serviceConfig;

    @InjectMocks
    private PriorityScoreService priorityScoreService;

    // this is not good
    private final int effortValue = 5;
    private final int impactValue = 4;
    private final int urgencyValue = 2;
    private final Map<ScoreType, Integer> weightMap = Map.of(
            ScoreType.EFFORT, 3,
            ScoreType.IMPACT, 2,
            ScoreType.URGENCY, 3
    );
    // TODO fix this
    private final ZoneId zoneId = ZoneId.of("UTC");

    @BeforeEach
    void setUp() {
        // TODO fix this
        when(serviceConfig.getZoneId()).thenReturn(zoneId);
        when(prioritiserConfig.getScoreWeightMap()).thenReturn(weightMap);
        when(prioritiserConfig.getScoreMaxValue()).thenReturn(10);
        when(prioritiserConfig.getDayDeadlineConstant()).thenReturn(3);
    }

    @Test
    void getPrioritisedTasks_ShouldReturnTasksPrioritisedByScore() {
        // rogue other scores can happen
    }

    @Test
    void getPrioritisedTasks_ShouldReturnTasksPrioritisedByScore_WithNoneDueToday() {
        //With these tasks
        Task task1 = createTaskWithIdAndDeadline(1L, Instant.now().minus(1, ChronoUnit.DAYS)); // 44
        Task task2 = createTaskWithIdAndDeadline(2L, Instant.now().minus(4, ChronoUnit.DAYS)); // 21.5
        Task task3 = createTaskWithIdAndDeadline(3L, Instant.now().minus(7, ChronoUnit.DAYS)); // 18.29
        Task task4 = createTaskWithIdAndDeadline(4L, Instant.now().plus(1, ChronoUnit.DAYS)); // 19
        Task task5 = createTaskWithIdAndDeadline(5L, Instant.now().plus(4, ChronoUnit.DAYS)); // 16.5
        Task task6 = createTaskWithIdAndDeadline(6L, Instant.now().plus(7, ChronoUnit.DAYS)); // 15.66
        List<Task> tasks = List.of(task1, task2, task3, task4, task5, task6);
        when(taskService.getAllTasks()).thenReturn(tasks);

        // When
        List<Task> prioritisedTasks = priorityScoreService.getPrioritisedTasks();

        // Then
        assertThat(prioritisedTasks).hasSize(6)
                .extracting(Task::getTaskId)
                .containsExactly(1L, 2L, 4L, 3L, 5L, 6L);
    }

    //test for the splitting of today
    @Test
    void getPrioritisedTasks_ShouldReturnTasksPrioritisedByDueTodayThenScore() {
        //With these tasks
        Task task1 = createTaskWithIdAndDeadline(1L, Instant.now().minus(1, ChronoUnit.HOURS)); // 29
        Task task2 = createTaskWithIdAndDeadline(2L, Instant.now().minus(4, ChronoUnit.DAYS)); // 21.5
        Task task3 = createTaskWithIdAndDeadline(3L, Instant.now().minus(7, ChronoUnit.DAYS)); // 18.29
        // TODO how? 4 should be bigger than 5?
        Task task4 = createTaskWithIdAndDeadline(4L, Instant.now().plus(2, ChronoUnit.HOURS)); // 19
        Task task5 = createTaskWithIdAndDeadline(5L, Instant.now().plus(4, ChronoUnit.HOURS)); // 21.5
        Task task6 = createTaskWithIdAndDeadline(6L, Instant.now().plus(7, ChronoUnit.DAYS)); // 15.66
        List<Task> tasks = List.of(task1, task2, task3, task4, task5, task6);
        when(taskService.getAllTasks()).thenReturn(tasks);

        // When
        List<Task> prioritisedTasks = priorityScoreService.getPrioritisedTasks();

        // Then
        assertThat(prioritisedTasks).hasSize(6)
                .extracting(Task::getTaskId)
                .containsExactly(1L, 5L, 4L, 2L, 3L, 6L);
    }


    @Test
    void calculatePriorityScore_ShouldReturnCorrectPriority_WithFutureDeadline() {
        //With
        Task task = TaskBuilder.create().build();

        // When
        double priorityScore = priorityScoreService.calculatePriorityScore(task, false);

        // int benefitScore = 4 * 2 + 2 * 3 = 14
        // TODO that's pretty small impact on overall score
        // int reliefScore = 5 * 3 / 6 + 3 = 1.666
        double expectPriorityScore = 15.66;

        // Then
        assertEquals(expectPriorityScore, priorityScore, 0.01);
    }

    @Test
    void calculatePriorityScore_ShouldReturnCorrectPriority_withHighEffort() {
        //With
        Task task = createTaskWithScores(ScoreType.EFFORT.create(10), ScoreType.IMPACT.create(impactValue), ScoreType.URGENCY.create(urgencyValue));

        // When
        double priorityScore = priorityScoreService.calculatePriorityScore(task, false);

        // int benefitScore = 14
        // int reliefScore = 10 * 3 / 6 + 3 = 3.333
        double expectPriorityScore = 17.33;

        // Then
        assertEquals(expectPriorityScore, priorityScore, 0.01);
    }

    @Test
    void calculatePriorityScore_ShouldReturnCorrectPriority_withHighUrgency() {
        //With
        Task task = createTaskWithScores(ScoreType.EFFORT.create(effortValue), ScoreType.IMPACT.create(impactValue), ScoreType.URGENCY.create(10));

        // When
        double priorityScore = priorityScoreService.calculatePriorityScore(task, false);

        // int benefitScore = 4 * 2 + 10 * 3 = 38
        // int reliefScore = 5 * 3 / 6 + 3 = 1.666
        double expectPriorityScore = 39.66;

        // Then
        assertEquals(expectPriorityScore, priorityScore, 0.01);
    }

    // TODO check the difference between priorities when deadline is set and when no deadline is set ...
    //  how much would a deadline task creep up if deadline closer? Are non deadline tasks favoured or ignored?
    // priority scale of changing deadline and effort - do weights need to be changed?

    @Test
    void calculatePriorityScore_ShouldReturnCorrectPriority_WithNoDeadline() {
        //With
        Task task = createTaskWithDeadline(null);

        // When
        double priorityScore = priorityScoreService.calculatePriorityScore(task, false);

        // int benefitScore =  14
        // int reliefScore = (10 - 5 + 1) * 3  =  18
        double expectPriorityScore = 32;

        // Then
        assertEquals(expectPriorityScore, priorityScore, 0.01);
    }

    @Test
    void calculatePriorityScore_ShouldReturnCorrectPriority_WithPastDeadline() {
        //With
        Task task = createTaskWithDeadline(Instant.now().minus(4, ChronoUnit.DAYS));

        // When
        double priorityScore = priorityScoreService.calculatePriorityScore(task, false);

        // int benefitScore =  14
        // int reliefScore = (5 * 3)/(-4 * -0.5) = 7.5
        double expectPriorityScore = 21.5;

        // Then
        assertEquals(expectPriorityScore, priorityScore, 0.01);
    }

    @Test
    void calculatePriorityScore_ShouldBeInfluencedByPastDeadlineMoreThanFuture() {
        //With tasks becoming more overdue
        Task oneDayOverDueTask = createTaskWithDeadline(Instant.now().minus(1, ChronoUnit.DAYS));
        Task fourDaysOverDueTask = createTaskWithDeadline(Instant.now().minus(4, ChronoUnit.DAYS));
        Task oneWeekOverDueTask = createTaskWithDeadline(Instant.now().minus(7, ChronoUnit.DAYS));

        // When priority score calculated
        // Then should have non-linear decrease in priority score
        // (the longer a task is not completed after the deadline the less important the deadline is)
        double oneDayOverDueTaskPriority = priorityScoreService.calculatePriorityScore(oneDayOverDueTask, false);
        assertEquals(44, oneDayOverDueTaskPriority, 0.01);

        double fourDaysOverDueTaskPriority = priorityScoreService.calculatePriorityScore(fourDaysOverDueTask, false);
        assertEquals(21.5, fourDaysOverDueTaskPriority, 0.01);

        double oneWeekOverDueTaskPriority = priorityScoreService.calculatePriorityScore(oneWeekOverDueTask, false);
        assertEquals(18.29, oneWeekOverDueTaskPriority, 0.01);

        // hmm not exactly what was wanted ... the more over you are the quicker you should fall
        // For a changes in increments of 3 the reductions are: 51% and 15%
        // small deadline vriable = better priority
        // when negative a small number should have little effect and large number bigger effect
        // small number -> small denominator so big relief score -> big priority
        // large number -> large denominator so small relief score -> small priority
        // not porpotional ... small numbers should stay smaller and big numers can do crazy

        //With tasks with deadlines moving further into the future
        Task oneDayToDoTask = createTaskWithDeadline(Instant.now().plus(1, ChronoUnit.DAYS));
        Task fourDaysToDoTask = createTaskWithDeadline(Instant.now().plus(4, ChronoUnit.DAYS));
        Task oneWeekToDoTask = createTaskWithDeadline(Instant.now().plus(7, ChronoUnit.DAYS));

        // When priority score calculated
        // Then future deadlines should have a proportionate impact
        double oneDayToDoTaskPriority = priorityScoreService.calculatePriorityScore(oneDayToDoTask, false);
        assertEquals(19, oneDayToDoTaskPriority, 0.01);

        double fourDaysToDoTaskPriority = priorityScoreService.calculatePriorityScore(fourDaysToDoTask, false);
        assertEquals(16.5, fourDaysToDoTaskPriority, 0.01);

        double oneWeekToDoTaskPriority = priorityScoreService.calculatePriorityScore(oneWeekToDoTask, false);
        assertEquals(15.66, oneWeekToDoTaskPriority, 0.01);

        // For a changes in increments of 3 the reductions are: 13% and 0.05%

    }

    @Test
    void calculatePriorityScore_ShouldReturnCorrectPriority_WithSameDayDeadline() {
        //With
        Task task = createTaskWithDeadline(Instant.now().plus(2, ChronoUnit.HOURS));

        // When
        double priorityScore = priorityScoreService.calculatePriorityScore(task, true);

        // int benefitScore =  14
        // int reliefScore = (5 * 3)/1 + 1  = 7.5
        double expectPriorityScore = 21.5;

        // Then
        assertEquals(expectPriorityScore, priorityScore, 0.01);
    }

    @Test
    void calculatePriorityScore_ShouldReturnCorrectPriority_WithSameDayOverdueDeadline() {
        //With
        Task task = createTaskWithDeadline(Instant.now().minus(2, ChronoUnit.HOURS));

        // When
        double priorityScore = priorityScoreService.calculatePriorityScore(task, true);

        // int benefitScore =  14
        // int reliefScore = (5 * 3)/0 + 1  = 15
        double expectPriorityScore = 29;

        // Then
        assertEquals(expectPriorityScore, priorityScore, 0.01);
    }

    @Test
    void calculatePriorityScore_ShouldReturnCorrectPriority_WithSamePriorityForOverdueTodayDeadline() {
        //With tasks becoming more overdue
        Task oneHourOverDueTask = createTaskWithDeadline(Instant.now().minus(1, ChronoUnit.HOURS));
        Task fourHourssOverDueTask = createTaskWithDeadline(Instant.now().minus(4, ChronoUnit.HOURS));
        Task sevenHourssOverDueTask = createTaskWithDeadline(Instant.now().minus(7, ChronoUnit.HOURS));

        // When priority score calculated
        // Then overdue same day deadlines should have a proportionate impact
        double oneHourOverdueTaskPriority = priorityScoreService.calculatePriorityScore(oneHourOverDueTask, true);
        assertEquals(29, oneHourOverdueTaskPriority, 0.01);

        double fourHoursOverdueTaskPriority = priorityScoreService.calculatePriorityScore(fourHourssOverDueTask, true);
        assertEquals(29, fourHoursOverdueTaskPriority, 0.01);

        double sevenHourOverdueTaskPriority = priorityScoreService.calculatePriorityScore(sevenHourssOverDueTask, true);
        assertEquals(29, sevenHourOverdueTaskPriority, 0.01);
    }

    // maybe one with id and deadline?
    private Task createTaskWithDeadline(Instant deadline) {
        return TaskBuilder.create().withDeadline(deadline).build();
    }

    private Task createTaskWithIdAndDeadline(Long id, Instant deadline) {
        return TaskBuilder.create().withTaskId(id).withDeadline(deadline).build();
    }

    private Task createTaskWithScores(Score effortScore, Score impactScore, Score urgencyScore) {
        return new Task(1L, "Task description", effortScore, impactScore, urgencyScore, Instant.now().plus(7, ChronoUnit.DAYS));
    }

}
