package com.example.taskprioritiser.internal.service;

import com.example.taskprioritiser.PrioritiserConfig;
import com.example.taskprioritiser.ServiceConfig;
import com.example.taskprioritiser.internal.service.model.ScoreType;
import com.example.taskprioritiser.internal.service.model.Task;
import com.example.taskprioritiser.internal.service.model.TaskPriority;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskPrioritisationService {

// TODO improve time zone handling

    private final TaskService taskService;
    private final PriorityScoringService priorityScoringService;

    public TaskPrioritisationService(TaskService taskService, PriorityScoringService priorityScoringService) {
        this.taskService = taskService;
        this.priorityScoringService = priorityScoringService;
    }

    public List<Task> getPrioritisedTasks() {

        List<Task> tasks = taskService.getAllTasks();

        LocalDateTime now = Instant.now().atZone(ZoneId.of("UTC")).toLocalDateTime();

        return tasks.stream()
                // get task priority properties
                .map(task -> priorityScoringService.getTaskPriority(task, now))
                // group tasks by whether they are due today
                .collect(Collectors.partitioningBy(TaskPriority::isToday,
                        Collectors.collectingAndThen(Collectors.toList(),
                                taskPriorities -> taskPriorities.stream()
                                        // sort each group by priority score
                                        .sorted(Comparator.comparingDouble(TaskPriority::getPriorityScore).reversed())
                                        // extract the task object as that's all that needs to be saved in the map
                                        .map(TaskPriority::getTask).toList())))
                // Collect the two lists together with the tasks due today first
                .entrySet().stream()
                // false would be first otherwise
                .sorted(Map.Entry.<Boolean, List<Task>>comparingByKey().reversed())
                .flatMap(entry -> entry.getValue().stream())
                .toList();
    }

}
