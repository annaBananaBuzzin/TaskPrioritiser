package com.example.taskprioritiser.internal.controller;

import com.example.taskprioritiser.api.TaskRequest;
import com.example.taskprioritiser.api.TaskResponse;
import com.example.taskprioritiser.api.ScoreType;
import com.example.taskprioritiser.api.TaskResource;
import com.example.taskprioritiser.internal.service.model.Task;
import com.example.taskprioritiser.internal.service.TaskService;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

// TODO intergration testing 

@RestController
public class TaskController implements TaskResource {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task createdTask = taskService.createTask(ExternalTaskMapper.fromCreation(taskRequest));
        return ExternalTaskMapper.serviceToResponse(createdTask);
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks().stream()
                .map(ExternalTaskMapper::serviceToResponse)
                .toList();
    }

    @Override
    //annotation for handling exception thrown
    public TaskResponse getTask(Long id) {
        return ExternalTaskMapper.serviceToResponse(taskService.getTask(id));
    }

    @Override
    public void updateTask(Long id, String description) {
        taskService.updateTaskDescription(id, description);
    }

    @Override
    public void updateTask(Long id, Instant taskDeadline) {
        // Cannot update deadline to a past time
        // swap the if statement around?
        if(taskDeadline.isAfter(Instant.now())) {
            taskService.updateTaskDeadline(id, taskDeadline);
        } else {
            throw new IllegalArgumentException("Deadline must be in the future");
        }
    }

    @Override
    public void updateTask(Long id, ScoreType score, int value) {
        taskService.updateTaskScore(id, ExternalScoreTypeMapper.toService(score), value);
    }

    // TODO add all score update endpoint

    // TODO add delete endpoint

}
