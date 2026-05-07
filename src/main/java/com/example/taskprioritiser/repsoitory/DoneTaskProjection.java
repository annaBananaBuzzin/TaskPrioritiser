package com.example.taskprioritiser.repsoitory;

import java.time.Instant;
import java.time.OffsetDateTime;

public interface DoneTaskProjection {

    Long getTaskDoneId();

        // Hidden internal getter that matches what the DB driver returns
    OffsetDateTime getDoneAt();

    Long getTaskId();

    String getDescription();

    Integer getEffort();

    Integer getImpact();

    Integer getUrgency();

    Instant getDeadline();

    // Public getter for your application logic
    default Instant getDoneAtInstant() {
        return getDoneAt() != null ? getDoneAt().toInstant() : null;
    }
}

