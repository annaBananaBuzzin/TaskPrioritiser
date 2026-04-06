package com.example.taskprioritiser.internal.service.repsoitory;

import com.example.taskprioritiser.internal.service.repsoitory.entity.TaskEntity;
import com.example.taskprioritiser.internal.service.model.ScoreType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TaskPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(TaskPersistenceService.class);
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
        logger.info("Before update - Getting task {}", taskID);
        TaskEntity beforeUpdate = taskRepository.fetchTaskById(taskID);
        logger.info("Before update - Description: {}", beforeUpdate.getDescription());

        validateUniqueDescription(description);
        logger.info("Calling repository updateDescription for task {} with new description: {}", taskID, description);
        taskRepository.updateDescription(taskID, description);

        logger.info("After update - Getting task {} to verify", taskID);
        TaskEntity afterUpdate = taskRepository.fetchTaskById(taskID);
        logger.info("After update - Description: {}", afterUpdate.getDescription());
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
