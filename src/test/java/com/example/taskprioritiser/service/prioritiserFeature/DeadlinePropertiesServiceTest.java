package com.example.taskprioritiser.service.prioritiserFeature;

import com.example.taskprioritiser.PrioritiserConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static com.example.taskprioritiser.TestHelper.Temporality.FUTURE;
import static com.example.taskprioritiser.TestHelper.Temporality.PAST;
import static com.example.taskprioritiser.TestHelper.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class DeadlinePropertiesServiceTest {

    @InjectMocks
    DeadlinePropertiesService underTest;

    private MockedStatic<PrioritiserConfig> configMock;

    private final LocalDateTime now = LocalDateTime.now(fixedClock);

    @BeforeEach
    public void setup() {
        // Create a mocked static class for PrioritiserConfig
        configMock = mockStatic(PrioritiserConfig.class);
        configMock.when(PrioritiserConfig::getDayDeadlineConstant).thenReturn(3);
    }

    @AfterEach
    public void tearDown() {
        if (configMock != null) {
            configMock.close();
        }
    }


    @Test
    void getDeadlineProperties_WithTodayDeadline() {
        Instant deadline = now.plusHours(2).atZone(ZoneId.of("UTC")).toInstant();
        DeadlineProperties deadlineProperties = underTest.getDeadlineProperties(deadline, now);
        assertThat(deadlineProperties).satisfies(props -> {
            assertTrue(props.isToday());
            assertEquals(2, props.getDurationUntilDeadline().toHours());
        });
    }

    @Test
    void getDeadlineProperties_WithNotTodayDeadline() {
        Instant deadline = now.plusDays(3).atZone(ZoneId.of("UTC")).toInstant();
        DeadlineProperties deadlineProperties = underTest.getDeadlineProperties(deadline, now);
        assertThat(deadlineProperties).satisfies(props -> {
            assertFalse(props.isToday());
            assertEquals(3, props.getDurationUntilDeadline().toDays());
        });
    }

    @Test
    void getDeadlineVariable_WithTodayDeadline() {
        Duration duration = FUTURE.createDuration(4, ChronoUnit.HOURS);
        DeadlineProperties deadlineProperties = new DeadlineProperties(true, duration);
        double variable = underTest.getDeadlineVariable(deadlineProperties);

        assertEquals(5, variable); // 4 hours + 1
    }

    @Test
    void getDeadlineVariable_WithOverdueTodayDeadline() {
        Duration duration = PAST.createDuration(8, ChronoUnit.HOURS);
        DeadlineProperties deadlineProperties = new DeadlineProperties(true, duration);
        double variable = underTest.getDeadlineVariable(deadlineProperties);

        assertEquals(1, variable);
    }

    @Test
    void getDeadlineVariable_WithFutureDeadline() {
        Duration duration = FUTURE.createDuration(5, ChronoUnit.DAYS);
        DeadlineProperties deadlineProperties = new DeadlineProperties(false, duration);
        double variable = underTest.getDeadlineVariable(deadlineProperties);

        assertEquals(8, variable);

    }

    @Test
    void getDeadlineVariable_WithOverdueDeadline() {
        Duration duration = PAST.createDuration(3, ChronoUnit.DAYS);
        DeadlineProperties deadlineProperties = new DeadlineProperties(false, duration);
        double variable = underTest.getDeadlineVariable(deadlineProperties);

        assertEquals(1.5, variable);
    }
}