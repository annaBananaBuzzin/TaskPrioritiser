package com.example.taskprioritiser.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaskPriority {

    private final Task task;
    private final boolean isToday;
    private final double priorityScore;

    public TaskPriority(Task task, double priorityScore) {
        this.task = task;
        this.isToday = false;
        this.priorityScore = priorityScore;
    }
}
