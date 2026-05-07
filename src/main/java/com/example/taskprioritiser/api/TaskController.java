package com.example.taskprioritiser.api;

import com.example.taskprioritiser.service.model.Task;
import com.example.taskprioritiser.service.TaskDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class TaskController implements TaskResource {

    private final TaskDetailService taskDetailService;

    public TaskController(TaskDetailService taskDetailService) {
        this.taskDetailService = taskDetailService;
    }

    @Override
    public TaskResponse createTask(TaskRequest taskRequest) {
    // Add test that the POJO can't be created if scores aren't valid
        Task createdTask = taskDetailService.createTask(ServiceTaskMapper.fromCreationRequest(taskRequest));
        return ServiceTaskMapper.ToResponse(createdTask);
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return List.of();
//        taskDetailService.getAllTasks().stream()
//                .map(ServiceTaskMapper::ToResponse)
//                .toList();
    }

    @Override
    public TaskResponse getTask(Long id) {
        return ServiceTaskMapper.ToResponse(taskDetailService.getTask(id));
    }

    @Override
    public void updateTask(Long id, UpdateDescriptionRequest newDescription) {
        taskDetailService.updateTaskDescription(id, newDescription.getNewDescription());
    }

    @Override
    public void updateTask(Long id, Instant taskDeadline) {
        taskDetailService.updateTaskDeadline(id, taskDeadline);
    }

    @Override
    public void updateTask(Long id, ScoreType score, int value) {
        taskDetailService.updateTaskScore(id, ExternalScoreTypeMapper.toService(score), value);
    }

    @Override
    public void deleteTask(Long id) {
        taskDetailService.deleteTask(id);
    }

    @Override
    public ErrorResponse handleNoSuchElementException(NoSuchElementException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @Override
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
}
