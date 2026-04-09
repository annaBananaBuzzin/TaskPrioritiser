package com.example.taskprioritiser.internal.controller;

import com.example.taskprioritiser.api.PrioritiserResource;
import com.example.taskprioritiser.api.TaskResponse;
import com.example.taskprioritiser.internal.service.TaskPrioritisationService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PrioritiserController implements PrioritiserResource {
    // TODO intergration test only

    private final TaskPrioritisationService taskPrioritisationService;

    public PrioritiserController(TaskPrioritisationService taskPrioritisationService) {
        this.taskPrioritisationService = taskPrioritisationService;
    }

    @Override
    public List<TaskResponse> getAllTasksPrioritised() {
        return taskPrioritisationService.getPrioritisedTasks().stream()
                .map(ExternalTaskMapper::serviceToResponse).toList();
    }
}
