package com.example.taskprioritiser.repsoitory;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="task")
// could be record but id is set after creation
public class TaskEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    // TODO - getter can throw null pointer exception if this is a new task without id
    private Long taskId;

    @NotBlank
    @Column(nullable=false, unique=true)
    private String description;

    // potentially could add Score class but don't see much value

    @Min(1)
    @Max(10)
    @Column(nullable=false)
    private int effort;

    @Min(1)
    @Max(10)
    @Column(nullable=false)
    private int impact;

    @Min(1)
    @Max(10)
    @Column(nullable=false)
    private int urgency;

    @Min(1)
    @Max(10)
    @Column
    private Instant deadline;

    public TaskEntity(String description, int effort, int impact, int urgency, Instant deadline) {
        this.description = description;
        this.effort = effort;
        this.impact = impact;
        this.urgency = urgency;
        this.deadline = deadline;
    }
}
