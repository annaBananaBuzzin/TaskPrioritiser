package com.example.taskprioritiser.repsoitory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class DoneTaskPersistenceService {

    private final DoneTaskRepository doneTaskRepository;

    public DoneTaskPersistenceService(DoneTaskRepository doneTaskRepository) {
        this.doneTaskRepository = doneTaskRepository;
    }

    @Transactional
    public void saveDoneTask(Long taskID) {
        doneTaskRepository.saveDoneTask(taskID, Instant.now());
    }

    @Transactional
    public boolean isTaskDone(Long taskID) {
        return doneTaskRepository.isTaskDone(taskID);
    }

    @Transactional
    public void deleteDoneTask(Long taskID) {
        doneTaskRepository.deleteByTaskId(taskID);
    }

    @Transactional
    public Optional<DoneTaskEntity> fetchDoneTaskByTaskId(Long taskID) {
        return Optional.ofNullable(doneTaskRepository.fetchDoneTaskByTaskId(taskID));
    }

    @Transactional
    public Optional<DoneTaskEntity> fetchDoneTaskByDescription(String description) {
        return Optional.ofNullable(doneTaskRepository.fetchDoneTaskByTaskDescription(description));
    }

    @Transactional
    public List<DoneTaskEntity> fetchAllDoneTasks() {
        return doneTaskRepository.fetchAllDoneTasks();
    }

    @Transactional
    public List<TaskEntity> fetchAllNotDoneTasks() {
    return doneTaskRepository.fetchAllNotDoneTasks();
    }

}
