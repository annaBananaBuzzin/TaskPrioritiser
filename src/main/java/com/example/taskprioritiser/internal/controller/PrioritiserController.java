package com.example.taskprioritiser.internal.controller;

import com.example.taskprioritiser.api.PrioritiserResource;
import com.example.taskprioritiser.api.TaskResponse;
import com.example.taskprioritiser.internal.service.PrioritiserService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PrioritiserController implements PrioritiserResource {

    private final PrioritiserService prioritiserService;

    public PrioritiserController(PrioritiserService prioritiserService) {
        this.prioritiserService = prioritiserService;
    }

    @Override
    public List<TaskResponse> getAllTasksPrioritised() {
        return prioritiserService.getPrioritisedTasks().stream()
                .map(ExternalTaskMapper::serviceToResponse).toList();
    }
}
