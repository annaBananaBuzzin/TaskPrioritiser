package com.example.taskprioritiser.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class TaskResponse {

    private final Long taskId;
    private final String description;
    private final int effort;
    private final int impact;
    private final int urgency;
    // this is optional but doing not null check where needed instead of making optional
    private final Instant deadline;

}
