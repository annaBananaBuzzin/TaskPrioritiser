package com.example.taskprioritiser.service;

import com.example.taskprioritiser.service.model.Task;
import org.assertj.core.util.TriFunction;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.List;

public class TestHelper {

// This is supposed to be set as the .now() in the application when running tests
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

}
