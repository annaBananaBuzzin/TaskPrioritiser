package com.example.taskprioritiser.service;

import com.example.taskprioritiser.repsoitory.TaskPersistenceService;
import com.example.taskprioritiser.service.mapper.EntityTaskMapper;
import com.example.taskprioritiser.service.model.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TasksManagementService {

    private final TaskPersistenceService taskPersistenceService;

    public TasksManagementService(TaskPersistenceService taskPersistenceService) {
        this.taskPersistenceService = taskPersistenceService;
    }

    public List<Task> getAllTasks() {
        return taskPersistenceService.getAllTasks().stream()
                .map(EntityTaskMapper::toService)
                .toList();
    }

    // get all tasks not done
    // got all tasks due today not done
}
