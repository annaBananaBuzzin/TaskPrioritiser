package com.example.taskprioritiser.service;

import com.example.taskprioritiser.internal.service.model.EffortScore;
import com.example.taskprioritiser.internal.service.model.ImpactScore;
import com.example.taskprioritiser.internal.service.model.ScoreType;
import com.example.taskprioritiser.internal.service.model.Task;
import com.example.taskprioritiser.internal.service.model.UrgencyScore;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TaskBuilder {

    private Long taskId = 1L;
    private String description = "Test Task";
    //The casting aint great
    private EffortScore effortScore = (EffortScore) ScoreType.EFFORT.create(5);
    private ImpactScore impactScore = (ImpactScore) ScoreType.IMPACT.create(4);
    private UrgencyScore urgencyScore = (UrgencyScore) ScoreType.URGENCY.create(2);
    private Instant deadline = Instant.now().plus(7, ChronoUnit.DAYS);

    public static TaskBuilder create() {
        return new TaskBuilder();
    }

    public TaskBuilder withTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }

    public TaskBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder withEffortScore(int value) {
        this.effortScore = (EffortScore) ScoreType.EFFORT.create(value);
        return this;
    }

    public TaskBuilder withImpactScore(int value) {
        this.impactScore = (ImpactScore) ScoreType.IMPACT.create(value);
        return this;
    }

    public TaskBuilder withUrgencyScore(int value) {
        this.urgencyScore = (UrgencyScore) ScoreType.URGENCY.create(value);
        return this;
    }

    public TaskBuilder withDeadline(Instant deadline) {
        this.deadline = deadline;
        return this;
    }

    public Task build() {
        return new Task(taskId, description, effortScore, impactScore, urgencyScore, deadline);
    }
}
