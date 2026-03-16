package com.example.taskprioritiser.controller;

import com.example.taskprioritiser.exposed.TaskRequest;
import com.example.taskprioritiser.exposed.TaskResponse;
import com.example.taskprioritiser.service.*;

// which packed should this be in to protect where the mapping can occur
public class ExternalTaskMapper {

    public static NewTask fromCreation(TaskRequest request){
        return new NewTask(request.getDescription(), new EffortScore(request.getEffort()), new ImpactScore(request.getImpact()), new UrgencyScore(request.getUrgency()), request.getDeadline());
    }

    public static Task requestToService(TaskRequest request){
        return new Task(request.getDescription(), request.getEffort(), request.getImpact(), request.getUrgency(), request.getDeadline());
    }

    public static TaskResponse serviceToResponse(Task task){
        return new TaskResponse(task.getTaskId(), task.getDescription(), task.getEffort(), task.getImpact(), task.getUrgency(), task.getDeadline());
    }

}
