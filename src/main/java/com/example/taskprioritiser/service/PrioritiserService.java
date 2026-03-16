package com.example.taskprioritiser.service;

public class PrioritiserService {

    // Add the fetching
    // Add the prioristiing
    // The weights of each variable

    private final TaskService taskService;

    public PrioritiserService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void getPrioritisedTasks(){
        taskService.getAllTasks();
        // gets weights for each score
    }

}
