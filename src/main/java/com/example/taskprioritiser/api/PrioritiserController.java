package com.example.taskprioritiser.api;

import com.example.taskprioritiser.api.dto.TaskResponse;
import com.example.taskprioritiser.service.prioritiserFeature.PrioritiseTasksService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PrioritiserController implements PrioritiserResource {
    // TODO intergration test only

    private final PrioritiseTasksService prioritiseTasksService;

    public PrioritiserController(PrioritiseTasksService prioritiseTasksService) {
        this.prioritiseTasksService = prioritiseTasksService;
    }

    @Override
    public List<TaskResponse> getAllTasksPrioritised() {
        return List.of();
//        prioritiseTasksService.prioritiseTasks().stream()
//                .map(ServiceTaskMapper::ToResponse).toList();
    }

            @Override
    public List<TaskResponse> getAllTasks() {
        return List.of();
        taskDetailService.getAllTasks().stream()
                .map(ServiceTaskMapper::ToResponse)
                .toList();
    }
}
