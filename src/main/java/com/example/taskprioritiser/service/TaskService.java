package com.example.taskprioritiser.service;

import com.example.taskprioritiser.repsoitory.TaskPersistenceService;
import com.example.taskprioritiser.service.mapper.EntityTaskMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TaskService {

    // TODO add logic for completeing tasks - would have a table of "outstanding tasks" fetched by a join

    private final TaskPersistenceService taskPersistenceService;

    public TaskService(TaskPersistenceService taskPersistenceService) {
        this.taskPersistenceService = taskPersistenceService;
    }

    // can throw illegal argument if description is not null
    public Task createTask(NewTask newTask) {
        // Validate inputs
        descriptionValidation(newTask.getDescription());
        validateScores(newTask.getEffortScore(), newTask.getImpactScore(), newTask.getUrgencyScore());
        deadlineValidation(newTask.getDeadline());

        // map to entity and persist
        Long newTaskId = taskPersistenceService.createTask(newTask.toEntityCreation());
        return taskPersistenceService.getTask(newTaskId).map(EntityTaskMapper::toService)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve the newly created task with ID: " + newTaskId));
    }

    public void updateTaskDescription(Long taskID, String description) {
        descriptionValidation(description);
        // TODO - fetch task to ensure unique
        taskPersistenceService.updateTaskDescription(taskID, description);
    }

//    public void updateTaskScores(Long taskID, int effort, int impact, int urgency) {
//        validateScores(effort, impact, urgency);
//        taskPersistenceService.updateTaskScores(taskID, effort, impact, urgency);
//    }

    public void updateTaskScore(Long taskID, ScoreType scoreType, int value) {
        Score newScore = scoreType.create(value);
        updateTaskScore(taskID, newScore);
    }

    public void updateTaskScore(Long taskID, Score score) {
        scoreValidation(score);
        taskPersistenceService.updateTaskScore(taskID, score.getType(), score.getValue());
    }

    public void updateTaskDeadline(Long taskID, Instant deadline) {
        deadlineValidation(deadline);
        taskPersistenceService.updateTaskDeadline(taskID, deadline);
    }

    public List<Task> getAllTasks() {
        return taskPersistenceService.getAllTasks().stream()
                .map(EntityTaskMapper::toService)
                .toList();
    }

    public Task getTask(Long taskID) {
        return taskPersistenceService.getTask(taskID)
                .map(EntityTaskMapper::toService)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskID));
    }

    private void descriptionValidation(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
    }

    public void validateScores(EffortScore effortScore,ImpactScore impactScore, UrgencyScore urgencyScore) {
        scoreValidation(effortScore);
        scoreValidation(impactScore);
        scoreValidation(urgencyScore);
    }

    private void scoreValidation(Score score){
        int value = score.getValue();
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException(score.getType() + " score must be between 1 and 10");
        }
    }

    private void deadlineValidation(Instant deadline) {
        if (deadline.isBefore(Instant.now())){
            throw new IllegalArgumentException("Deadline must be in the future");
        }
    }

}
