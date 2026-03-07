package com.example.taskprioritiser.service;

import java.time.Instant;
import java.util.Optional;

public class Task {

    // added later?
    int taskId;
    String description;
    int effort;
    int impact;
    int urgency;
    Instant deadline;

    public Task(String description, int effort, int impact, int urgency, Instant deadline) {
        this.description = description;
        this.effort = effort;
        this.impact = impact;
        this.urgency = urgency;
        this.deadline = deadline;
    }

    // if added later than this too should be optional
    public int getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEffort() {
        return effort;
    }

    public void setEffort(int effort) {
        this.effort = effort;
    }

    public int getImpact() {
        return impact;
    }

    public void setImpact(int impact) {
        this.impact = impact;
    }

    public int getUrgency() {
        return urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public Optional<Instant> getDeadline() {
        return Optional.of(deadline);
    }

    public void setDeadline(Instant deadline) {
        this.deadline = deadline;
    }
}
