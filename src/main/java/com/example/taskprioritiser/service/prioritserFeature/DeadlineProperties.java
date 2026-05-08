package com.example.taskprioritiser.service.prioritserFeature;

import lombok.Getter;

import java.time.Duration;

public class DeadlineProperties {

    @Getter
    private final boolean isToday;
    @Getter
    private final Duration durationUntilDeadline;

    public DeadlineProperties(boolean isToday, Duration durationUntilDeadline) {
        this.isToday = isToday;
        this.durationUntilDeadline = durationUntilDeadline;
    }

    public boolean isOverdue() {
        return durationUntilDeadline.isNegative();
    }

}
