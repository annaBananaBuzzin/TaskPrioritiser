package com.example.taskprioritiser.service;

import com.example.taskprioritiser.repsoitory.TaskPersistenceService;
import com.example.taskprioritiser.service.mapper.EntityTaskMapper;
import com.example.taskprioritiser.service.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class TaskService {

    // TODO add logic for completeing tasks - would have a table of "outstanding tasks" fetched by a join
    // Should probably check id exists before doing anything

    private final TaskPersistenceService taskPersistenceService;

    public TaskService(TaskPersistenceService taskPersistenceService) {
        this.taskPersistenceService = taskPersistenceService;
    }

    public Task createTask(NewTask newTask) {
        // Validate inputs
        descriptionValidation(newTask.getDescription());
        deadlineValidation(newTask.getDeadline());

        // map to entity and persist
        Long newTaskId = taskPersistenceService.createTask(newTask.toEntityCreation());
        return taskPersistenceService.getTask(newTaskId).map(EntityTaskMapper::toService)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve the newly created task with ID: " + newTaskId));
    }

    public void updateTaskDescription(Long taskID, String description) {
        descriptionValidation(description);
        // TODO - fetch task to ensure unique here or in persistence layer? Surely this is a business logic concern
        taskPersistenceService.updateTaskDescription(taskID, description);
    }

    public void updateTaskScore(Long taskID, ScoreType scoreType, int value) {
    // this now does the score value validation
        Score newScore = scoreType.create(value);
        taskPersistenceService.updateTaskScore(taskID, newScore.getType(), newScore.getValue());
    }

    public void updateTaskDeadline(Long taskID, Instant deadline) {
        deadlineValidation(deadline);
        taskPersistenceService.updateTaskDeadline(taskID, deadline);
    }

    public void deleteTask(Long taskID) {
        try {
            getTask(taskID);
        } catch (Exception e) {
            log.warn("Attempted to delete non-existent task with ID: {}", taskID);
            return;
        }
        taskPersistenceService.deleteTask(taskID);
    }

    public List<Task> getAllTasks() {
        return taskPersistenceService.getAllTasks().stream()
                .map(EntityTaskMapper::toService)
                .toList();
    }

    public Task getTask(Long taskID) {
        return taskPersistenceService.getTask(taskID)
                .map(EntityTaskMapper::toService)
                .orElseThrow(() -> new NoSuchElementException("Task not found with ID: " + taskID));
    }

    private void descriptionValidation(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
    }

    private void deadlineValidation(Instant deadline) {
        if (deadline != null && deadline.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Deadline must be in the future");
        }
    }

}
