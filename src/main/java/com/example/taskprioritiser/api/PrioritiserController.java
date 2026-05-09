package com.example.taskprioritiser.api;

import com.example.taskprioritiser.api.dto.TaskResponse;
import com.example.taskprioritiser.service.prioritiserFeature.PrioritiserService;
import com.example.taskprioritiser.service.task.TasksManagementService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PrioritiserController implements PrioritiserResource {
    // TODO intergration test only

    private final PrioritiserService prioritiserService;
    private final TasksManagementService tasksManagementService;

    public PrioritiserController(PrioritiserService prioritiserService, TasksManagementService tasksManagementService) {
        this.prioritiserService = prioritiserService;
        this.tasksManagementService = tasksManagementService;
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return tasksManagementService.getAllTasks().stream()
                .map(ServiceTaskMapper::ToResponse)
                .toList();
    }

    @Override
    public List<TaskResponse> getAllOverdueTasks() {
        return tasksManagementService.getAllOverdueTasks().stream()
                .map(ServiceTaskMapper::ToResponse)
                .toList();
    }

    @Override
    public List<TaskResponse> getAllOutstandingTasksPrioritised() {
        return prioritiserService.getOutstandingTasksPrioritised().stream()
                .map(ServiceTaskMapper::ToResponse)
                .toList();
    }

    @Override
    public List<TaskResponse> getAllOutstandingTasksDueTodayPrioritised() {
        return prioritiserService.getOutstandingTasksDueTodayPrioritised().stream()
                .map(ServiceTaskMapper::ToResponse)
                .toList();
    }

}
