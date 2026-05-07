package com.example.taskprioritiser.repsoitory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task_done")
public class DoneTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskDoneId;

    @Column(nullable = false)
    private Instant doneAt;

    @Column(nullable = false)
    private Long taskId;

    public DoneTaskEntity(Long taskId, Instant doneAt) {
        this.taskId = taskId;
        this.doneAt = doneAt;
    }
}

