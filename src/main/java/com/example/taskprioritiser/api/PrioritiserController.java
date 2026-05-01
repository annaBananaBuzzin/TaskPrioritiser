package com.example.taskprioritiser.api;

import com.example.taskprioritiser.service.TaskPrioritisationService;
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
