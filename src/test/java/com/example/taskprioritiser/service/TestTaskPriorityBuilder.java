package com.example.taskprioritiser.service;

import com.example.taskprioritiser.service.model.TaskPriority;

public class TestTaskPriorityBuilder {

    private boolean isDueToday = false;
    private double priorityScore = 15;
    private TestTaskBuilder taskBuilder = TestTaskBuilder.create();

    public static TestTaskPriorityBuilder create() {
        return new TestTaskPriorityBuilder();
    }

    public TestTaskPriorityBuilder withIsDueToday() {
        this.isDueToday = true;
        return this;
    }

    public TestTaskPriorityBuilder withPriorityScore(double priorityScore) {
        this.priorityScore = priorityScore;
        return this;
    }

    public TestTaskPriorityBuilder withTaskId(Long taskId) {
        this.taskBuilder.withTaskId(taskId);
        return this;
    }

    public TaskPriority build() {
        return new TaskPriority(taskBuilder.build(), isDueToday, priorityScore);
    }
}
