package com.example.taskprioritiser.service;

import org.assertj.core.util.TriFunction;

import java.time.Instant;
import java.time.temporal.TemporalUnit;

public class TestTimeHelper {

    public static Instant createTime(
        TriFunction<Instant, Long, TemporalUnit, Instant> operator,
        long amount,
        TemporalUnit unit
    ) {
        return operator.apply(Instant.now(), amount, unit);
    }
}
