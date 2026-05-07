package com.example.taskprioritiser.service.prioritserFeature;

import com.example.taskprioritiser.PrioritiserConfig;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DeadlineProperties {

// TODO test

    @Getter
    private final boolean isToday;
    private final Duration durationUntilDeadline;

    public DeadlineProperties(Instant deadline, LocalDateTime now) {
        LocalDateTime deadlineDateTime = instantToLocalDateTime(deadline);
        this.isToday = deadlineDateTime.toLocalDate().equals(now.toLocalDate());
        this.durationUntilDeadline = Duration.between(now, deadlineDateTime);
    }

    public double getDeadlineVariable() {
        return isToday ? getTimeDueVariable(durationUntilDeadline) : getDateDueVariable(durationUntilDeadline);
    }

    private double getTimeDueVariable(Duration duration) {
        long hours = duration.toHours();
        return (hours < 0 ? 0 : hours) + 1;
    }

    private double getDateDueVariable(Duration duration) {
        long days = duration.toDays();
        int dayOffset = PrioritiserConfig.getDayDeadlineConstant();
        // if due tomorrow will be 0 days so the deadline constant helps without offsets and impossible equations
        return days < 0 ? days * -0.5 : days + dayOffset;
    }

    private LocalDateTime instantToLocalDateTime(Instant instant) {
        if (instant == null) {
            throw new IllegalArgumentException("Instant cannot be null");
        }
        // Improve time zone handling
        return instant.atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

}
