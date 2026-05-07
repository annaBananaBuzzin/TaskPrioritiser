package com.example.taskprioritiser.repsoitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DoneTaskRepository extends JpaRepository<DoneTaskEntity, Long> {

    @Modifying
    @Query(value = "INSERT INTO task_done (task_id, done_at) VALUES (:taskId, :doneAt)", nativeQuery = true)
    void saveDoneTask(@Param("taskId") Long taskId, @Param("doneAt") Instant doneAt);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM task_done WHERE task_id = :taskId)", nativeQuery = true)
    boolean isTaskDone(@Param("taskId") Long taskID);

    @Modifying
    @Query(value = "DELETE FROM task_done WHERE task_id = :taskId", nativeQuery = true)
    void deleteByTaskId(@Param("taskId") Long taskId);

    @Query(value = "SELECT td.task_done_id, td.done_at, t.task_id, t.description, t.effort, t.impact, t.urgency, t.deadline FROM task_done td INNER JOIN task t ON td.task_id = t.task_id WHERE t.task_id = :taskId", nativeQuery = true)
    DoneTaskProjection fetchDoneTaskByTaskId(@Param("taskId") Long taskID);

    @Query(value = "SELECT td.task_done_id, td.done_at, t.task_id, t.description, t.effort, t.impact, t.urgency, t.deadline FROM task_done td INNER JOIN task t ON td.task_id = t.task_id WHERE t.description = :description", nativeQuery = true)
    DoneTaskProjection fetchDoneTaskByTaskDescription(@Param("description") String description);

    @Query(value = "SELECT td.task_done_id, td.done_at, t.task_id, t.description, t.effort, t.impact, t.urgency, t.deadline FROM task_done td INNER JOIN task t ON td.task_id = t.task_id", nativeQuery = true)
    List<DoneTaskProjection> fetchAllDoneTasks();

    @Query(value = "SELECT t.* FROM task t LEFT JOIN task_done td ON t.task_id = td.task_id WHERE td.task_id IS NULL", nativeQuery = true)
    List<TaskEntity> fetchAllNotDoneTasks();
}
