package com.example.taskprioritiser.service.task;

import com.example.taskprioritiser.repsoitory.DoneTaskPersistenceService;
import com.example.taskprioritiser.repsoitory.TaskPersistenceService;
import com.example.taskprioritiser.service.mapper.EntityTaskMapper;
import com.example.taskprioritiser.service.model.Task;
import com.example.taskprioritiser.service.prioritiserFeature.DeadlineProperties;
import com.example.taskprioritiser.service.prioritiserFeature.DeadlinePropertiesService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TasksManagementService {

    private final TaskPersistenceService taskPersistenceService;

    private final DoneTaskPersistenceService doneTaskPersistenceService;

    private final DeadlinePropertiesService deadlinePropertiesService;

    public TasksManagementService(TaskPersistenceService taskPersistenceService, DoneTaskPersistenceService doneTaskPersistenceService, DeadlinePropertiesService deadlinePropertiesService) {
        this.taskPersistenceService = taskPersistenceService;
        this.doneTaskPersistenceService = doneTaskPersistenceService;
        this.deadlinePropertiesService = deadlinePropertiesService;
    }

    public void markTaskAsDone(Long taskID) {
        if (taskPersistenceService.getTask(taskID).isEmpty()) {
            throw new NoSuchElementException("Task not found with ID: " + taskID);
        }
        doneTaskPersistenceService.saveDoneTask(taskID);
    }

    public void unmarkTaskAsDone(Long taskID) {
        if (taskPersistenceService.getTask(taskID).isEmpty()) {
            throw new NoSuchElementException("Task not found with ID: " + taskID);
        }
        doneTaskPersistenceService.deleteDoneTask(taskID);
    }

    public List<Task> getAllTasks() {
        return taskPersistenceService.getAllTasks().stream()
                .map(EntityTaskMapper::toService)
                .toList();
    }

    public List<Task> getAllOutstandingTasks() {
        return doneTaskPersistenceService.fetchAllNotDoneTasks().stream()
                .map(EntityTaskMapper::toService)
                .toList();
    }

    public List<Task> getAllOutstandingTasksDueToday() {
        LocalDateTime now = Instant.now().atZone(ZoneId.of("UTC")).toLocalDateTime();
        return doneTaskPersistenceService.fetchAllNotDoneTasks().stream()
                .filter(task -> {
                    DeadlineProperties deadlineProperties = deadlinePropertiesService.getDeadlineProperties(task.getDeadline(), now);
                    return deadlineProperties.isToday();
                })
                .map(EntityTaskMapper::toService)
                .toList();
    }

    public List<Task> getAllOverdueTasks() {
        LocalDateTime now = Instant.now().atZone(ZoneId.of("UTC")).toLocalDateTime();
        return doneTaskPersistenceService.fetchAllNotDoneTasks().stream()
                .filter(task -> {
                    DeadlineProperties deadlineProperties = deadlinePropertiesService.getDeadlineProperties(task.getDeadline(), now);
                    return deadlineProperties.isOverdue();
                })
                .map(EntityTaskMapper::toService)
                .toList();
    }

}
