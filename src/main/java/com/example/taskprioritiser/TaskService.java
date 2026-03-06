package com.example.taskprioritiser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskService {

    // where does id comeinto it?
    // Task request and Task response - for the API layer
    // interanl task should always have id

    // should the validation occure in the other service?
    // What does the response/request layer do?


     private TaskRepositoryService taskRepositoryService;

     public TaskService(TaskRepositoryService taskRepositoryService) {
          this.taskRepositoryService = taskRepositoryService;
     }


    public void createTask(Task task) {
        // Validate inputs
        validateTaskVariables(task);

        // Save task to repository (not implemented here)
        taskRepositoryService.addTask(task.getDescription(), task.getEffort(), task.getImpact(), task.getUrgency(), task.getDeadline());

    }

    public void updateTaskDescription(int taskID, String description) {
        descriptionValidation(description);
        taskRepositoryService.updateDescription(taskID, description);
    }

    public void updateTaskScores(int taskID, int effort, int impact, int urgency) {
         validateScores(effort, impact, urgency);
         taskRepositoryService.updateScores(taskID, effort, impact, urgency);
    }

    public void updateTaskDeadline(int taskID, LocalDateTime deadline) {
        deadlineValidation(Optional.of(deadline));
        taskRepositoryService.updateDeadline(taskID, deadline);
    }

    public void updateTask(Task task) {
        // Validate inputs
        // TODO task validation - score variables should have @ annotation to validate them when set
        validateTaskVariables(task);

        // Update task in repository (not implemented here)
        taskRepositoryService.updateTask(task.getTaskId(), task.getDescription(), task.getEffort(), task.getImpact(), task.getUrgency(), task.getDeadline());

    }

    public List<Task> getAllTasks() {
        // Fetch all tasks from repository (not implemented here)
        return taskRepositoryService.getAllTasks();
    }

    public Task getTask(int taskID) {
        // Fetch task from repository (not implemented here)
        return taskRepositoryService.getTaskById(taskID);
    }

    private void validateTaskVariables(Task task){
        descriptionValidation(task.getDescription());
        validateScores(task);
        deadlineValidation(task.getDeadline());

    }

    private void descriptionValidation(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
    }

    private void validateScores(Task task) {
        effortScoreValidation(task.getEffort());
        impactScoreValidation(task.getImpact());
        urgencyScoreValidation(task.getUrgency());
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

    private void deadlineValidation(Optional<LocalDateTime> deadline) {
        if (deadline.isPresent() && deadline.get().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Deadline must be in the future");
        }
    }

    private void scoreValidation(int score, String errorMessage) {
        if (score < 1 || score > 10) {
            throw new IllegalArgumentException(errorMessage + " score must be between 1 and 10");
        }

    }

}
