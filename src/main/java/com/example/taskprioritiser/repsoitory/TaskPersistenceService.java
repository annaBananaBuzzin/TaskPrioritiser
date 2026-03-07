package com.example.taskprioritiser.repsoitory;

import com.example.taskprioritiser.service.Task;

import java.time.Instant;
import java.util.List;

// this is persistance layer
public class TaskPersistenceService {

    // where does id comeinto it?
    // this should be where the entity is created
    // eneity has the @ constraints so the validations here would be redundent in that case



     private final TaskRepository taskRepository;

     public TaskPersistenceService(TaskRepository taskRepository) {
          this.taskRepository = taskRepository;
     }


    public void createTask(TaskEntity taskEntity) {
        // Validate inputs
        validateTaskVariables(taskEntity);

        // Save task to repository (not implemented here)
        taskRepository.insertTask(taskEntity.getTaskId(),taskEntity.getDescription(), taskEntity.getEffort(), taskEntity.getImpact(), taskEntity.getUrgency(), taskEntity.getDeadline().orElse(null));

    }

    public void updateTaskDescription(int taskID, String description) {
        descriptionValidation(description);
        taskRepository.updateDescription(taskID, description);
    }

    public void updateTaskScores(int taskID, int effort, int impact, int urgency) {
         validateScores(effort, impact, urgency);
        taskRepository.updateScores(taskID, effort, impact, urgency);
    }

    public void updateTaskDeadline(int taskID, Instant deadline) {
        deadlineValidation(deadline);
        taskRepository.updateDeadline(taskID, deadline);
    }

    public void updateTask(TaskEntity taskEntity) {
        // Validate inputs
        validateTaskVariables(taskEntity);

        taskRepository.updateTask(taskEntity.getTaskId(), taskEntity.getDescription(), taskEntity.getEffort(), taskEntity.getImpact(), taskEntity.getUrgency(), taskEntity.getDeadline().orElse(null));

    }

    public List<Task> getAllTasks() {
        return taskRepository.fetchAllTasks();
    }

    public Task getTask(int taskID) {
        return taskRepository.fetchTaskById(taskID);
    }

    private void validateTaskVariables(TaskEntity taskEntity){
        descriptionValidation(taskEntity.getDescription());
        validateScores(taskEntity);
        if(taskEntity.getDeadline().isPresent()){
             deadlineValidation(taskEntity.getDeadline().get());
        }
    }

    private void descriptionValidation(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
    }

    private void validateScores(TaskEntity taskEntity) {
        effortScoreValidation(taskEntity.getEffort());
        impactScoreValidation(taskEntity.getImpact());
        urgencyScoreValidation(taskEntity.getUrgency());
    }

    public void validateScores(int effort,int impact, int urgency){
        effortScoreValidation(effort);
        impactScoreValidation(impact);
        urgencyScoreValidation(urgency);
    }

    private void effortScoreValidation(int effort) {
        scoreValidation(effort, "Effort");
    }

    private void impactScoreValidation(int impact) {
        scoreValidation(impact, "Impact");
    }

    private void urgencyScoreValidation(int urgency) {
        scoreValidation(urgency, "Urgency");
    }

    private void deadlineValidation(Instant deadline) {
        if (deadline.isBefore(Instant.now())){
            throw new IllegalArgumentException("Deadline must be in the future");
        }
    }

    private void scoreValidation(int score, String errorMessage) {
        if (score < 1 || score > 10) {
            throw new IllegalArgumentException(errorMessage + " score must be between 1 and 10");
        }
    }

}
