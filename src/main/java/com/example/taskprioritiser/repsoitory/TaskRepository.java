package com.example.taskprioritiser.repsoitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    // FYI – Using native queries purely as a reminder

    // TODO - improve table dynamics by creating a table for each score and connecting to task
    // TODO - whats the deal with value mapping and entities

    @Modifying
    @Query(value = "UPDATE task SET description = :description " +
            "WHERE task_id = :task_id", nativeQuery = true)
    void updateDescription(@Param("task_id")Long taskId, @Param("description")String description);

    @Modifying
    @Query(value = "UPDATE task SET effort = :effort " +
            "WHERE task_id = :task_id", nativeQuery = true)
    void updateEffortScore(@Param("task_id")Long taskId, @Param("effort")int effort);

    @Modifying
    @Query(value = "UPDATE task SET impact = :impact " +
            "WHERE task_id = :task_id", nativeQuery = true)
    void updateImpactScore(@Param("task_id")Long taskId, @Param("impact")int impact);

    @Modifying
    @Query(value = "UPDATE task SET urgency = :urgency " +
            "WHERE task_id = :task_id", nativeQuery = true)
    void updateUrgencyScore(@Param("task_id")Long taskId, @Param("urgency")int urgency);

    @Modifying
    @Query(value = "UPDATE task SET deadline = :deadline " +
            "WHERE task_id = :task_id", nativeQuery = true)
    void updateDeadline(@Param("task_id")Long taskId, @Param("deadline")Instant deadline);

    @Query(value = "SELECT * FROM task", nativeQuery = true)
    List<TaskEntity> fetchAllTasks();

    @Query(value = "SELECT * FROM task WHERE task_id = :id", nativeQuery = true)
    TaskEntity fetchTaskById(@Param("id") Long taskID);

    @Query(value = "SELECT * FROM task WHERE description = :description", nativeQuery = true)
    TaskEntity fetchTaskByDescription(@Param("description")String description);
}
