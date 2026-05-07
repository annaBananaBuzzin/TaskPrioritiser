package com.example.taskprioritiser;

import com.example.taskprioritiser.service.TestTaskBuilder;
import com.example.taskprioritiser.service.model.Task;
import org.assertj.core.util.TriFunction;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Random;

public class TestHelper {

// This is supposed to be set as the .now() in the application when running tests
// Should be injecting clock in service and then mocking
    public static Clock fixedClock = Clock.fixed(Instant.parse("2222-06-01T13:00:00Z"), ZoneOffset.UTC);

    public static Instant createTime(
            TriFunction<Instant, Long, TemporalUnit, Instant> operator,
            long amount,
            TemporalUnit unit
    ) {
        return operator.apply(Instant.now(fixedClock), amount, unit);
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
