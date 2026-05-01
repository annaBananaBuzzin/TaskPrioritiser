package com.example.taskprioritiser.api;

import com.example.taskprioritiser.service.model.*;

// which packed should this be in to protect where the mapping can occur
public class ExternalTaskMapper {

    public static NewTask fromCreation(TaskRequest request){
        return new NewTask(request.getDescription(), new EffortScore(request.getEffort()), new ImpactScore(request.getImpact()), new UrgencyScore(request.getUrgency()), request.getDeadline());
    }

//    public static Task requestToService(TaskRequest request){
//        return new Task(request.getDescription(), request.getEffort(), request.getImpact(), request.getUrgency(), request.getDeadline());
//    }

    public static TaskResponse serviceToResponse(Task task){
        return new TaskResponse(task.getTaskId(), task.getDescription(), task.getEffortScore().getValue(), task.getImpactScore().getValue(), task.getUrgencyScore().getValue(), task.getDeadline());
    }

}
