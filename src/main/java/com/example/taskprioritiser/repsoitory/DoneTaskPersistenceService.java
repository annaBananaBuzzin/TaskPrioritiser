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
    // Service layer to handle if invalid taskID
    @Transactional
    public void saveDoneTask(Long taskID) {
            DoneTaskEntity doneTaskEntity=  new DoneTaskEntity(taskID, Instant.now());

        doneTaskRepository.save(doneTaskEntity);
    }

    // Service layer to handle if invalid taskID
    @Transactional
    public boolean isTaskDone(Long taskID) {
        return doneTaskRepository.isTaskDone(taskID);
    }

    // Service layer to handle if invalid taskID
    @Transactional
    public void deleteDoneTask(Long taskID) {
        doneTaskRepository.deleteByTaskId(taskID);
    }

    @Transactional
    public Optional<DoneTaskProjection> fetchDoneTaskByTaskId(Long taskID) {
        return Optional.ofNullable(doneTaskRepository.fetchDoneTaskByTaskId(taskID));
    }

    @Transactional
    public Optional<DoneTaskProjection> fetchDoneTaskByDescription(String description) {
        return Optional.ofNullable(doneTaskRepository.fetchDoneTaskByTaskDescription(description));
    }

    @Transactional
    public List<DoneTaskProjection> fetchAllDoneTasks() {
        return doneTaskRepository.fetchAllDoneTasks();
    }

    @Transactional
    public List<TaskEntity> fetchAllNotDoneTasks() {
    return doneTaskRepository.fetchAllNotDoneTasks();
    }

}
