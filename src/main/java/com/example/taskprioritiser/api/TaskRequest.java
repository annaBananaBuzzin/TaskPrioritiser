package com.example.taskprioritiser.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
// TODO change to record
@AllArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Task description is required")
    private final String description;

    // boiler plate and validaton messages
    @Min(value=1, message = "Score must be at least 1")
    @Max(value=10, message = "Score must be at most 10")
    private final int effort;

    @Min(value=1, message = "Score must be at least 1")
    @Max(value=10, message = "Score must be at most 10")
    private final int impact;

    @Min(value=1, message = "Score must be at least 1")
    @Max(value=10, message = "Score must be at most 10")
    private final int urgency;

    // TODO this is an optional
    private final Instant deadline;

}
