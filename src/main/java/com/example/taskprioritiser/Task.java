package com.example.taskprioritiser;

import java.time.LocalDateTime;
import java.util.Optional;

public class Task {

    // added later?
    int taskId;
    String description;
    int effort;
    int impact;
    int urgency;
    LocalDateTime deadline;

    public Task(String description, int effort, int impact, int urgency, LocalDateTime deadline) {
        this.description = description;
        this.effort = effort;
        this.impact = impact;
        this.urgency = urgency;
        this.deadline = deadline;
    }

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

    public Optional<LocalDateTime> getDeadline() {
        return Optional.of(deadline);
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}
