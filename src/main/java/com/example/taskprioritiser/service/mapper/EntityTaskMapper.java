package com.example.taskprioritiser.service.mapper;

import com.example.taskprioritiser.repsoitory.TaskEntity;
import com.example.taskprioritiser.service.EffortScore;
import com.example.taskprioritiser.service.ImpactScore;
import com.example.taskprioritiser.service.Task;
import com.example.taskprioritiser.service.UrgencyScore;

public class EntityTaskMapper {

    // pass getter in as method reference?
    public static Task toService(TaskEntity entity) {
        return new Task(entity.getTaskId(), entity.getDescription(),
                new EffortScore(entity.getEffort()), new ImpactScore(entity.getImpact()), new UrgencyScore(entity.getUrgency()), entity.getDeadline());
    }

    public static TaskEntity fromService(Task task) {
        return new TaskEntity(task.getTaskId(), task.getDescription(), task.getEffortScore().getValue(), task.getImpactScore().getValue(), task.getUrgencyScore().getValue(), task.getDeadline());
    }

}
