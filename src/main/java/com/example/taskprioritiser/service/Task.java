package com.example.taskprioritiser.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class Task {

    // TODO - getter can throw null pointer exception if this is a new task without id
    private final Long taskId;
    private String description;
    private EffortScore effortScore;
    private ImpactScore impactScore;
    private UrgencyScore urgencyScore;
    private Instant deadline;

    // add get methods for each score value

}
