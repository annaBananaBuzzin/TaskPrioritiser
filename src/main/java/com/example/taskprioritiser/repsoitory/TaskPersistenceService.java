package com.example.taskprioritiser.repsoitory;

import com.example.taskprioritiser.service.model.ScoreType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TaskPersistenceService {

    private final TaskRepository taskRepository;

    public TaskPersistenceService(TaskRepository taskRepository) {
          this.taskRepository = taskRepository;
     }

     @Transactional
    public Long createTask(TaskEntity taskEntity) {
         validateUniqueDescription(taskEntity.getDescription());
        TaskEntity saved = taskRepository.save(taskEntity);
        return saved.getTaskId();
    }

    @Transactional
    public void updateTaskDescription(Long taskID, String description) {
        // will throw if updating to exact same description ... seems a bit unnecessary
        validateUniqueDescription(description);

        taskRepository.updateDescription(taskID, description);
    }

    @Transactional
    public void updateTaskScore(Long taskID, ScoreType scoreType, int value) {
        switch (scoreType) {
            case EFFORT -> taskRepository.updateEffortScore(taskID, value);
            case IMPACT -> taskRepository.updateImpactScore(taskID, value);
            case URGENCY -> taskRepository.updateUrgencyScore(taskID, value);
        }
    }

    @Transactional
    public void updateTaskDeadline(Long taskID, Instant deadline) {
        taskRepository.updateDeadline(taskID, deadline);
    }

    @Transactional
    public void deleteTask(Long taskID) {
    // If task doesn't exist this is will be silent
        taskRepository.deleteById(taskID);
    }

    @Transactional
    public List<TaskEntity> getAllTasks() {
        return taskRepository.fetchAllTasks();
    }

    @Transactional
    public Optional<TaskEntity> getTask(Long taskID) {
        return Optional.ofNullable(taskRepository.fetchTaskById(taskID));
    }

    // TODO move to service layer
    // As this is a column constraint, would this never happen? Should this be in the service layer?
    private void validateUniqueDescription(String description) {
        if (taskRepository.fetchTaskByDescription(description) != null) {
            throw new IllegalArgumentException("Description must be unique");
        }
    }
}
