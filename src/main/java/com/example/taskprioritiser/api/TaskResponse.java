package com.example.taskprioritiser.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
// TODO change to record
public class TaskResponse {

    private final Long taskId;
    private final String description;
    private final int effort;
    private final int impact;
    private final int urgency;
    // this is optional
    private final Instant deadline;

}
