package com.example.taskprioritiser.api;

import com.example.taskprioritiser.service.model.Task;
import com.example.taskprioritiser.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

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
    public TaskResponse getTask(Long id) {
        return ExternalTaskMapper.serviceToResponse(taskService.getTask(id));
    }

    @Override
    public void updateTask(Long id, UpdateDescriptionRequest newDescription) {
        taskService.updateTaskDescription(id, newDescription.getNewDescription());
    }

    @Override
    public void updateTask(Long id, Instant taskDeadline) {
        taskService.updateTaskDeadline(id, taskDeadline);
    }

    @Override
    public void updateTask(Long id, ScoreType score, int value) {
        taskService.updateTaskScore(id, ExternalScoreTypeMapper.toService(score), value);
    }

    // TODO add all score update endpoint

    // TODO add delete endpoint


    @Override
    public ErrorResponse handleNoSuchElementException(NoSuchElementException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @Override
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
}
