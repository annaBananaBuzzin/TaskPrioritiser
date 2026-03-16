package com.example.taskprioritiser.service;

import com.example.taskprioritiser.repsoitory.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
// todo connect to Task class
public class NewTask {

    private final String description;
    private final EffortScore effortScore;
    private final ImpactScore impactScore;
    private final UrgencyScore urgencyScore;
    private final Instant deadline;

    // this classes sole purpose is to insert task into DB
    // mapping straight to entity within class is okay as function is tightly coupled to use of class
    public TaskEntity toEntityCreation() {
        return new TaskEntity(description, effortScore.getValue(), impactScore.getValue(), urgencyScore.getValue(), deadline);
    }

}
