package com.example.taskprioritiser.service.prioritiserFeature;

import com.example.taskprioritiser.PrioritiserConfig;
import com.example.taskprioritiser.TestHelper;
import com.example.taskprioritiser.service.TestTaskBuilder;
import com.example.taskprioritiser.service.model.ScoreType;
import com.example.taskprioritiser.service.model.Task;
import com.example.taskprioritiser.service.model.TaskPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.taskprioritiser.TestHelper.Temporality.FUTURE;
import static com.example.taskprioritiser.TestHelper.Temporality.PAST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriorityScoringServiceTest {

    private LocalDateTime now = Instant.now(TestHelper.fixedClock).atZone(ZoneId.of("UTC")).toLocalDateTime();

    private final Map<ScoreType, Integer> weightMap = Map.of(
            ScoreType.EFFORT, 3,
            ScoreType.IMPACT, 2,
            ScoreType.URGENCY, 3
    );

    @Mock
    private DeadlinePropertiesService deadlinePropertiesService;

    @InjectMocks
    private PriorityScoringService priorityScoringService;

    private MockedStatic<PrioritiserConfig> configMock;

    @BeforeEach
    public void setup() {
        // Create a mocked static class for PrioritiserConfig
        configMock = mockStatic(PrioritiserConfig.class);
        configMock.when(PrioritiserConfig::getScoreWeightMap).thenReturn(weightMap);
    }

    @AfterEach
    public void tearDown() {
        if (configMock != null) {
            configMock.close();
        }
    }

    @Test
    void getTaskPriority_ShouldReturnCorrectPriority_WithFutureDeadline() {
        //With
        Task task = TestTaskBuilder.create().build();
        mockDefaultDeadlineProperties(task.getDeadline());


        // When
        TaskPriority taskPriority = priorityScoringService.getTaskPriority(task, now);

        // int benefitScore = 4 * 2 + 2 * 3 = 14
        // TODO that's pretty small impact on overall score
        // int reliefScore = 5 * 3 / 7 + 3 = 1.5
        double expectPriorityScore = 15.5;

        //Then
        assertThat(taskPriority).satisfies(t -> {
                    assertFalse(t.isToday());
                    assertEquals(expectPriorityScore, t.getPriorityScore(), 0.01);
                }
        );
    }

    @Test
    void getTaskPriority_ShouldReturnCorrectPriority_withHighEffort() {
        //With
        Task task = TestTaskBuilder.create().withEffortScore(10).build();
        mockDefaultDeadlineProperties(task.getDeadline());

        // When
        TaskPriority taskPriority = priorityScoringService.getTaskPriority(task, now);

        // int benefitScore = 14
        // int reliefScore = 10 * 3 / 7 + 3 = 3
        double expectPriorityScore = 17;

        // Then
        assertThat(taskPriority).satisfies(t -> {
                    assertFalse(t.isToday());
                    assertEquals(expectPriorityScore, t.getPriorityScore(), 0.01);
                }
        );
    }

    @Test
    void getTaskPriority_ShouldReturnCorrectPriority_withHighUrgency() {
        //With
        Task task = TestTaskBuilder.create().withUrgencyScore(10).build();
        mockDefaultDeadlineProperties(task.getDeadline());

        // When
        TaskPriority taskPriority = priorityScoringService.getTaskPriority(task, now);

        // int benefitScore = 4 * 2 + 10 * 3 = 38
        // int reliefScore = 5 * 3 / 7 + 3 = 1.5
        double expectPriorityScore = 39.5;

        // Then
        assertThat(taskPriority).satisfies(t -> {
                    assertFalse(t.isToday());
                    assertEquals(expectPriorityScore, t.getPriorityScore(), 0.01);
                }
        );
    }

    // TODO check the difference between priorities when deadline is set and when no deadline is set ...
    //  how much would a deadline task creep up if deadline closer? Are non deadline tasks favoured or ignored?
    // priority scale of changing deadline and effort - do weights need to be changed?

    @Test
    void getTaskPriority_ShouldReturnCorrectPriority_WithNoDeadline() {
        //With
        Task task = TestTaskBuilder.create().withDeadline(null).build();

        // When
        TaskPriority taskPriority = priorityScoringService.getTaskPriority(task, now);

        // int benefitScore =  14
        // int reliefScore = (10 - 5) * 3  =  15
        double expectPriorityScore = 29;

        // Then
        assertThat(taskPriority).satisfies(t -> {
                    assertFalse(t.isToday());
                    assertEquals(expectPriorityScore, t.getPriorityScore(), 0.01);
                }
        );
    }

    @Test
    void getTaskPriority_ShouldReturnCorrectPriority_WithPastDeadline() {
        //With
        Task task = setUpMockDataForTaskWithDeadline(PAST, 4, ChronoUnit.DAYS, 2.0);

        // When
        TaskPriority taskPriority = priorityScoringService.getTaskPriority(task, now);

        // int benefitScore =  14
        // int reliefScore = (5 * 3)/(-4 * -0.5) = 7.5
        double expectPriorityScore = 21.5;

        // Then
        // Then
        assertThat(taskPriority).satisfies(t -> {
                    assertFalse(t.isToday());
                    assertEquals(expectPriorityScore, t.getPriorityScore(), 0.01);
                }
        );
    }

    @Test
    void getTaskPriority_ShouldBeInfluencedByPastDeadlineMoreThanFuture() {
        //With tasks becoming more overdue
        Task oneDayOverDueTask = setUpMockDataForTaskWithDeadline(PAST, 1, ChronoUnit.DAYS, 0.5);
        Task twoDaysOverDueTask = setUpMockDataForTaskWithDeadline(PAST, 2, ChronoUnit.DAYS, 1.0);
        Task fourDaysOverDueTask = setUpMockDataForTaskWithDeadline(PAST, 4, ChronoUnit.DAYS, 2.0);
        Task oneWeekOverDueTask = setUpMockDataForTaskWithDeadline(PAST, 7, ChronoUnit.DAYS, 3.5);

        // When priority score calculated
        // Then should have non-linear decrease in priority score
        // (the longer a task is not completed after the deadline the less important the deadline is)
        TaskPriority oneDayOverDueTaskPriority = priorityScoringService.getTaskPriority(oneDayOverDueTask, now);
        assertThat(oneDayOverDueTaskPriority).satisfies(t -> {
            assertFalse(t.isToday());
            assertEquals(44, t.getPriorityScore(), 0.01);
        });

        TaskPriority twoDaysOverDueTaskPriority = priorityScoringService.getTaskPriority(twoDaysOverDueTask, now);
        assertThat(twoDaysOverDueTaskPriority).satisfies(t -> {
            assertFalse(t.isToday());
            assertEquals(29, t.getPriorityScore(), 0.01);
        });

        TaskPriority fourDaysOverDueTaskPriority = priorityScoringService.getTaskPriority(fourDaysOverDueTask, now);
        assertThat(fourDaysOverDueTaskPriority).satisfies(t -> {
            assertFalse(t.isToday());
            assertEquals(21.5, t.getPriorityScore(), 0.01);
        });

        TaskPriority oneWeekOverDueTaskPriority = priorityScoringService.getTaskPriority(oneWeekOverDueTask, now);
        assertThat(oneWeekOverDueTaskPriority).satisfies(t -> {
            assertFalse(t.isToday());
            assertEquals(18.29, t.getPriorityScore(), 0.01);
        });

        //With tasks with deadlines moving further into the future
        Task oneDayToDoTask = setUpMockDataForTaskWithDeadline(FUTURE, 1, ChronoUnit.DAYS, 4.0);
        Task fourDaysToDoTask = setUpMockDataForTaskWithDeadline(FUTURE, 4, ChronoUnit.DAYS, 5.0);
        Task oneWeekToDoTask = setUpMockDataForTaskWithDeadline(FUTURE, 7, ChronoUnit.DAYS, 8.0);

        // When priority score calculated
        // Then future deadlines should have a proportionate impact
        TaskPriority oneDayToDoTaskPriority = priorityScoringService.getTaskPriority(oneDayToDoTask, now);
        assertThat(oneDayToDoTaskPriority).satisfies(t -> {
            assertFalse(t.isToday());
            assertEquals(17.75, t.getPriorityScore(), 0.01);
        });

        TaskPriority fourDaysToDoTaskPriority = priorityScoringService.getTaskPriority(fourDaysToDoTask, now);
        assertThat(fourDaysToDoTaskPriority).satisfies(t -> {
            assertFalse(t.isToday());
            assertEquals(16.14, t.getPriorityScore(), 0.01);
        });

        TaskPriority oneWeekToDoTaskPriority = priorityScoringService.getTaskPriority(oneWeekToDoTask, now);
        assertThat(oneWeekToDoTaskPriority).satisfies(t -> {
            assertFalse(t.isToday());
            assertEquals(15.5, t.getPriorityScore(), 0.01);
        });

    }

    @Test
    void getTaskPriority_ShouldReturnCorrectPriority_WithSameDayDeadline() {
        //With
        Task task = setUpMockDataForTaskWithDeadline(FUTURE, 2, ChronoUnit.HOURS, 3);

        // When
        TaskPriority priorityScore = priorityScoringService.getTaskPriority(task, now);

        // int benefitScore =  14
        // int reliefScore = (5 * 3)/2 + 1  = 5
        double expectPriorityScore = 19;

        // Then
        assertThat(priorityScore).satisfies(t -> {
            assertTrue(t.isToday());
            assertEquals(expectPriorityScore, t.getPriorityScore(), 0.01);
        });
    }

    @Test
    void getTaskPriority_ShouldReturnCorrectPriority_WithSameDayOverdueDeadline() {
        //With
        Task task = setUpMockDataForTaskWithDeadline(PAST, 2, ChronoUnit.HOURS, 1.0);

        // When
        TaskPriority priorityScore = priorityScoringService.getTaskPriority(task, now);

        // int benefitScore =  14
        // int reliefScore = (5 * 3)/0 + 1  = 15
        double expectPriorityScore = 29;

        // Then
        assertThat(priorityScore).satisfies(t -> {
            assertTrue(t.isToday());
            assertEquals(expectPriorityScore, t.getPriorityScore(), 0.01);
        });
    }

    @Test
    void getTaskPriority_ShouldReturnCorrectPriority_WithSamePriorityForOverdueTodayDeadline() {
        //With tasks becoming more overdue
        Task oneHourOverDueTask = setUpMockDataForTaskWithDeadline(PAST, 1, ChronoUnit.HOURS, 1.0);
        Task fourHourssOverDueTask = setUpMockDataForTaskWithDeadline(PAST, 4, ChronoUnit.HOURS, 1.0);
        Task sevenHourssOverDueTask = setUpMockDataForTaskWithDeadline(PAST, 7, ChronoUnit.HOURS, 1.0);

        // When priority score calculated
        // Then overdue same day deadlines should have a proportionate impact
        TaskPriority oneHourOverdueTaskPriority = priorityScoringService.getTaskPriority(oneHourOverDueTask, now);
        assertThat(oneHourOverdueTaskPriority).satisfies(t -> {
            assertTrue(t.isToday());
            assertEquals(29, t.getPriorityScore(), 0.01);
        });

        TaskPriority fourHoursOverdueTaskPriority = priorityScoringService.getTaskPriority(fourHourssOverDueTask, now);
        assertThat(fourHoursOverdueTaskPriority).satisfies(t -> {
            assertTrue(t.isToday());
            assertEquals(29, t.getPriorityScore(), 0.01);
        });

        TaskPriority sevenHourOverdueTaskPriority = priorityScoringService.getTaskPriority(sevenHourssOverDueTask, now);
        assertThat(sevenHourOverdueTaskPriority).satisfies(t -> {
            assertTrue(t.isToday());
            assertEquals(29, t.getPriorityScore(), 0.01);
        });
    }

    @Test
    void getTaskPriority_ShouldReturnCorrectPriority_ForOverdueTodayDeadlineWithDifferingScores() {
        //With tasks
        // Impact score means should be higher priority regardless of overdue deadline today
        Task task1 = TestTaskBuilder.create()
                .withDeadline(PAST.createTime(3, ChronoUnit.HOURS))
                .withImpactScore(8)
                .build();
        Task task2 = setUpMockDataForTaskWithDeadline(PAST, 7, ChronoUnit.HOURS, 1.0);


        TaskPriority taskPriority1 = priorityScoringService.getTaskPriority(task1, now);
        assertThat(taskPriority1).satisfies(t -> {
            assertTrue(t.isToday());
            assertEquals(37, t.getPriorityScore(), 0.01);
        });

        TaskPriority taskPriority2 = priorityScoringService.getTaskPriority(task2, now);
        assertThat(taskPriority2).satisfies(t -> {
            assertTrue(t.isToday());
            assertEquals(29, t.getPriorityScore(), 0.01);
        });
    }

    private Task setUpMockDataForTaskWithDeadline(TestHelper.Temporality temporality, long amount, TemporalUnit unit, double expectedDeadlineVariable) {
        Instant deadline = temporality.createTime(amount, unit);
        DeadlineProperties deadlineProperties = mockDurationUntilDeadline(temporality, amount, unit, deadline);
        when(deadlinePropertiesService.getDeadlineVariable(eq(deadlineProperties))).thenReturn(expectedDeadlineVariable);
        return TestTaskBuilder.create().withDeadline(deadline).build();
    }

    private void mockDefaultDeadlineProperties(Instant deadline) {
        mockDurationUntilDeadline(FUTURE, 7, ChronoUnit.DAYS, deadline);
        when(deadlinePropertiesService.getDeadlineVariable(any())).thenReturn(10.0);
    }

    private DeadlineProperties mockDurationUntilDeadline(TestHelper.Temporality temporality, long amount, TemporalUnit unit, Instant deadline) {
        // the duration doesn't actually matter
        Duration duration = temporality.createDuration(amount, unit);
        DeadlineProperties deadlineProperties = new DeadlineProperties(unit == ChronoUnit.HOURS, duration);
        when(deadlinePropertiesService.getDeadlineProperties(eq(deadline), any())).thenReturn(deadlineProperties);
        return deadlineProperties;
    }

}
