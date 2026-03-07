package com.example.taskprioritiser.service;

import com.example.taskprioritiser.repsoitory.TaskEntity;
import com.example.taskprioritiser.repsoitory.TaskPersistenceService;

import java.time.Instant;
import java.util.List;

public class TaskService {

    private final TaskPersistenceService taskPersistenceService;

    public TaskService(TaskPersistenceService taskPersistenceService) {
        this.taskPersistenceService = taskPersistenceService;
    }

    // This will use internal Task POJO
    // logic class is the only one that goes between
    // think Re/Res stay in controller as that is exposed externally it should stay outside of internal logic
    // entity stays in persistence layer

    // Validation (currently duplicated until entity annotations added)
    // the request and response objects need to be validated to
    // ensure the other side is sending the right data and is recieveing the expected data
    // do you create the internal object and then do the validation again?
    // validate before or after creaton, surely you need to validate before creation
    // but  want creation in cronller but don't want controller to hve validation
    // the balance between forcing and good coding practice
    // currently this is well simple so doesn't matter, good practice would depend on the situatin and wider working stule
    // best practice – ensuring the param past is consistent for that one use case right?
    // if service methods start accepting request dto for every type of request there may have to be an addtional endpoint
    // where pojos start getting changed to fit other narratives

    // Q: which service has the respnsibility of reating internal class
    // Q: which service has the respobsibly of creating the entity?


    public void createTask(Task task) {
        // Validate inputs
        validateTaskVariables(task);

        // Save task to repository (not implemented here)
        taskPersistenceService.createTask(task.getTaskId(),task.getDescription(), task.getEffort(), task.getImpact(), task.getUrgency(),
                //need to update this
                task.getDeadline().orElse(null));

    }

    public void updateTaskDescription(int taskID, String description) {
        descriptionValidation(description);
        taskPersistenceService.updateTaskDescription(taskID, description);
    }

    public void updateTaskScores(int taskID, int effort, int impact, int urgency) {
        validateScores(effort, impact, urgency);
        taskPersistenceService.updateTaskScores(taskID, effort, impact, urgency);
    }

    public void updateTaskDeadline(int taskID, Instant deadline) {
        deadlineValidation(deadline);
        taskPersistenceService.updateTaskDeadline(taskID, deadline);
    }

    public void updateTask(Task task) {
        // Validate inputs
        validateTaskVariables(task);

        taskPersistenceService.updateTask(task.getTaskId(), task.getDescription(), task.getEffort(), task.getImpact(), task.getUrgency(),
                // need to update this once checked
                task.getDeadline().orElse(null));

    }

    public List<Task> getAllTasks() {
        return taskPersistenceService.getAllTasks();
    }

    public Task getTask(int taskID) {
        return taskPersistenceService.getTask(taskID);
    }

    private void validateTaskVariables(Task task){
        descriptionValidation(task.getDescription());
        validateScores(task);
        if(task.getDeadline().isPresent()){
            deadlineValidation(task.getDeadline().get());
        }
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
