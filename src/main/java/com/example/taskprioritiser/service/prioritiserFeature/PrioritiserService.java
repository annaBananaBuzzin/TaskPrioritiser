package com.example.taskprioritiser.service.prioritiserFeature;

import com.example.taskprioritiser.service.model.Task;
import com.example.taskprioritiser.service.task.TasksManagementService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrioritiserService {

    private final TasksManagementService tasksManagementService;
    private final PrioritiseTasksService prioritiseTasksService;

    public PrioritiserService(TasksManagementService tasksManagementService, PrioritiseTasksService prioritiseTasksService) {
        this.tasksManagementService = tasksManagementService;
        this.prioritiseTasksService = prioritiseTasksService;
    }

    public List<Task> getOutstandingTasksPrioritised() {
        List<Task> outstandingTasks = tasksManagementService.getAllOutstandingTasks();
        return prioritiseTasksService.prioritiseTasks(outstandingTasks);
    }

    public List<Task> getOutstandingTasksDueTodayPrioritised() {
        List<Task> outstandingTasksDueToday = tasksManagementService.getAllOutstandingTasksDueToday();
        return prioritiseTasksService.prioritiseTasks(outstandingTasksDueToday);
    }
}
