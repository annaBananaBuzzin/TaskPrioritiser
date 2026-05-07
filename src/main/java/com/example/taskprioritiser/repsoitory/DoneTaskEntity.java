package com.example.taskprioritiser.repsoitory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class DoneTaskEntity {

    private Long taskDoneId;

    private Instant doneAt;

    private Long taskId;

    private String description;

    private int effort;

    private int impact;

    private int urgency;

    private Instant deadline;

}
