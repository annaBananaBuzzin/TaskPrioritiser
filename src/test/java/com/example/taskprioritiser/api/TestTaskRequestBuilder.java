package com.example.taskprioritiser.api;

import com.example.taskprioritiser.service.TestHelper;
import com.example.taskprioritiser.service.model.ScoreType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TestTaskRequestBuilder {

    private String description = "Test Task Request";
    private int effortScore = 5;
    private int impactScore = 4;
    private int urgencyScore = 2;
    private Instant deadline = Instant.now(TestHelper.fixedClock).plus(7, ChronoUnit.DAYS);

    public static TestTaskRequestBuilder create() {
        return new TestTaskRequestBuilder();
    }

    public TestTaskRequestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public TestTaskRequestBuilder withEffortScore(int value) {
        this.effortScore = value;
        return this;
    }

    public TestTaskRequestBuilder withImpactScore(int value) {
        this.impactScore = value;
        return this;
    }

    public TestTaskRequestBuilder withUrgencyScore(int value) {
        this.urgencyScore = value;
        return this;
    }

    public TestTaskRequestBuilder withDeadline(Instant deadline) {
        this.deadline = deadline;
        return this;
    }

    public TaskRequest build() {
        return new TaskRequest(description, effortScore, impactScore, urgencyScore, deadline);
    }
}
