package com.example.taskprioritiser.repsoitory;

import jakarta.persistence.Entity;

import java.time.Instant;
import java.util.Optional;

@Entity
public class TaskEntity {

    // TODO
    //when using @entity in a spring book project when does the preimary key id get set, qwhen creating the entity of when inserting into table
    // check if entities should have setters

    // added later?
    int taskId;
    String description;
    int effort;
    int impact;
    int urgency;
    // Check how optionals work best with entities
    Instant deadline;

    public TaskEntity(String description, int effort, int impact, int urgency, Instant deadline) {
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

    public int getEffort() {
        return effort;
    }

    public int getImpact() {
        return impact;
    }


    public int getUrgency() {
        return urgency;
    }

    public Optional<Instant> getDeadline() {
        return Optional.ofNullable(deadline);
    }

}
