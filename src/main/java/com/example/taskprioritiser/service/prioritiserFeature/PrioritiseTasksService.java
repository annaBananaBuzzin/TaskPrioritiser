package com.example.taskprioritiser.service.prioritiserFeature;

import com.example.taskprioritiser.service.model.Task;
import com.example.taskprioritiser.service.model.TaskPriority;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PrioritiseTasksService {

// TODO improve time zone handling

    private final PriorityScoringService priorityScoringService;

    public PrioritiseTasksService(PriorityScoringService priorityScoringService) {
        this.priorityScoringService = priorityScoringService;
    }

    public List<Task> prioritiseTasks(List<Task> tasks) {
        // Improve time zone handling
        LocalDateTime now = Instant.now().atZone(ZoneId.of("UTC")).toLocalDateTime();

        return tasks.stream()
                // get task priority properties
                .map(task -> priorityScoringService.getTaskPriority(task, now))
                // group tasks by whether they are due today
                // this should be in task management service
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
