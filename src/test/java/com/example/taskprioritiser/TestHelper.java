package com.example.taskprioritiser;

import com.example.taskprioritiser.service.TestTaskBuilder;
import com.example.taskprioritiser.service.model.Task;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.assertj.core.util.TriFunction;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Random;

public class TestHelper {

    // This is supposed to be set as the .now() in the application when running tests
// Should be injecting clock in service and then mocking
    public static Clock fixedClock = Clock.fixed(Instant.parse("2222-06-01T13:00:00Z"), ZoneOffset.UTC);

    @AllArgsConstructor
    public enum Temporality {
        PAST(Instant::minus, -1), FUTURE(Instant::plus, 1);

        private final TriFunction<Instant, Long, TemporalUnit, Instant> operator;
        private final long directionMultiplier;

        public Instant createTime(long amount, TemporalUnit unit) {
            return operator.apply(Instant.now(fixedClock), amount, unit);
        }

        public Duration createDuration(long amount, TemporalUnit unit) {
            return Duration.of(amount * directionMultiplier, unit);

        }
    }

    public static List<Task> getDefaultTasks() {
        return List.of(
                TestTaskBuilder.create().withTaskId(1L).build(),
                TestTaskBuilder.create().withTaskId(2L).build(),
                TestTaskBuilder.create().withTaskId(3L).build()
        );
    }

    public static Long getRandomIdExcluding(List<Long> excludedIds) {
        Random random = new Random();
        Long candidate;

        do {
            candidate = random.nextLong(1, Long.MAX_VALUE);
        } while (excludedIds.contains(candidate));

        return candidate;
    }
}
