package com.example.taskprioritiser.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Deprecated
public class TaskScoreValue {

    @NotBlank(message = "Score name is required")
    private final String scoreName;

    @Min(value=1, message = "Value must be at least 0")
    @Max(value=10, message = "Score must be at most 10")
    private final int scoreWeight;

}
