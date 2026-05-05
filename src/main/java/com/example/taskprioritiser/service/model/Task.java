package com.example.taskprioritiser.service.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Task {

    // TODO - getter can throw null pointer exception if this is a new task without id
    // this is only applicabel for entity task .. a service task should always have an id
    // just don't have a get method for id on entity.. but how to map to service? directly reference the field
    private final Long taskId;
    private String description;
    // would this be a list
    private EffortScore effortScore;
    private ImpactScore impactScore;
    private UrgencyScore urgencyScore;
    // this can be null
    private Instant deadline;

    public Task(Long taskId, String description, Score effortScore, Score impactScore, Score urgencyScore, Instant deadline) {
        this.taskId = taskId;
        this.description = description;
        this.effortScore = (EffortScore) effortScore;
        this.impactScore = (ImpactScore) impactScore;
        this.urgencyScore = (UrgencyScore) urgencyScore;
        this.deadline = deadline;
    }

    public int getEffortScoreValue() {
        return effortScore.getValue();
    }

    public int getImpactScoreValue() {
        return impactScore.getValue();
    }

    public int getUrgencyScoreValue() {
        return urgencyScore.getValue();
    }

}
