package com.example.taskprioritiser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskRepositoryService {
    // connecttion to DB


    public void addTask(String description, int effort, int impact, int urgency, Optional<LocalDateTime> deadline) {
    }

    public void updateTask(int id, String description, int effort, int impact, int urgency, Optional<LocalDateTime> deadline) {
    }

    public void updateDescription(int taskID, String description) {
    }

    public void updateScores(int id, int effort, int impact, int urgency) {
    }

    public void updateDeadline(int id, LocalDateTime deadline) {
    }

    public List<Task> getAllTasks() {
        return null;
    }

    public Task getTaskById(int taskID) {
        return null;
    }
}
