package com.example.taskprioritiser.service.prioritiserFeature;

import com.example.taskprioritiser.PrioritiserConfig;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DeadlinePropertiesService {

    public DeadlineProperties getDeadlineProperties(Instant deadline, LocalDateTime now) {
        if (deadline == null) {
            return new DeadlineProperties(false, null);
        }
        LocalDateTime dateTimeDeadline = instantToLocalDateTime(deadline);

        boolean isToday = dateTimeDeadline.toLocalDate().equals(now.toLocalDate());

        Duration durationUntilDeadline = Duration.between(now, dateTimeDeadline);

        return new DeadlineProperties(isToday, durationUntilDeadline);
    }

    public double getDeadlineVariable(DeadlineProperties deadlineProperties) {
        return deadlineProperties.isToday() ? getTimeDueVariable(deadlineProperties.getDurationUntilDeadline()) : getDateDueVariable(deadlineProperties.getDurationUntilDeadline());
    }

    private LocalDateTime instantToLocalDateTime(Instant instant) {
        // Improve time zone handling
        return instant.atZone(ZoneId.of("UTC")).toLocalDateTime();
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
}
