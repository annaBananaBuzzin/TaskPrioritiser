package com.example.taskprioritiser.api;

import com.example.taskprioritiser.service.model.*;

public class ServiceTaskMapper {

    public static NewTask fromCreationRequest(TaskRequest request){
        return new NewTask(request.getDescription(), new EffortScore(request.getEffort()), new ImpactScore(request.getImpact()), new UrgencyScore(request.getUrgency()), request.getDeadline());
    }

    public static TaskResponse ToResponse(Task task){
        return new TaskResponse(task.getTaskId(), task.getDescription(), task.getEffortScoreValue(), task.getImpactScoreValue(), task.getUrgencyScoreValue(), task.getDeadline());
    }

}
