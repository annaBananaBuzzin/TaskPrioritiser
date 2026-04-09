package com.example.taskprioritiser.internal.controller;

import com.example.taskprioritiser.api.PrioritiserResource;
import com.example.taskprioritiser.api.TaskResponse;
import com.example.taskprioritiser.internal.service.PriorityScoreService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PrioritiserController implements PrioritiserResource {
    // TODO intergration test only

    private final PriorityScoreService priorityScoreService;

    public PrioritiserController(PriorityScoreService priorityScoreService) {
        this.priorityScoreService = priorityScoreService;
    }

    @Override
    public List<TaskResponse> getAllTasksPrioritised() {
        return priorityScoreService.getPrioritisedTasks().stream()
                .map(ExternalTaskMapper::serviceToResponse).toList();
    }
}
