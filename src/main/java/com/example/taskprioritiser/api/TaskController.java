package com.example.taskprioritiser.api;

import com.example.taskprioritiser.api.dto.ScoreType;
import com.example.taskprioritiser.api.dto.TaskRequest;
import com.example.taskprioritiser.api.dto.TaskResponse;
import com.example.taskprioritiser.api.dto.UpdateDescriptionRequest;
import com.example.taskprioritiser.service.model.Task;
import com.example.taskprioritiser.service.task.TaskDetailService;
import com.example.taskprioritiser.service.task.TasksManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;


// TODO move mapping to service layer
@RestController
public class TaskController implements TaskResource {

    private final TaskDetailService taskDetailService;

    private final TasksManagementService tasksManagementService;

    public TaskController(TaskDetailService taskDetailService, TasksManagementService tasksManagementService) {
        this.taskDetailService = taskDetailService;
        this.tasksManagementService = tasksManagementService;
    }

    @Override
    public TaskResponse createTask(TaskRequest taskRequest) {
    // Add test that the POJO can't be created if scores aren't valid
        Task createdTask = taskDetailService.createTask(ServiceTaskMapper.fromCreationRequest(taskRequest));
        return ServiceTaskMapper.ToResponse(createdTask);
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
    public void markTaskAsDone(Long id) {

    }

    @Override
    public void markTaskAsUndone(Long id) {

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
