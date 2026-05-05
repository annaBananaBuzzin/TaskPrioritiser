package com.example.taskprioritiser.service.mapper;

import com.example.taskprioritiser.repsoitory.TaskEntity;
import com.example.taskprioritiser.service.model.EffortScore;
import com.example.taskprioritiser.service.model.ImpactScore;
import com.example.taskprioritiser.service.model.Task;
import com.example.taskprioritiser.service.model.UrgencyScore;

public class EntityTaskMapper {

    public static Task toService(TaskEntity entity) {
        return new Task(entity.getTaskId(), entity.getDescription(),
                new EffortScore(entity.getEffort()), new ImpactScore(entity.getImpact()), new UrgencyScore(entity.getUrgency()), entity.getDeadline());
    }

    public static TaskEntity fromService(Task task) {
        return new TaskEntity(task.getTaskId(), task.getDescription(), task.getEffortScoreValue(), task.getImpactScoreValue(), task.getUrgencyScoreValue(), task.getDeadline());
    }

}
