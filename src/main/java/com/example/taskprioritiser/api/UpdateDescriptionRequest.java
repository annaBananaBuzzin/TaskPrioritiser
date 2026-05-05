package com.example.taskprioritiser.api;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateDescriptionRequest {

    @NotBlank(message = "Description must not be blank")
    private final String newDescription;

}
