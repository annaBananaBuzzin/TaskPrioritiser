package com.example.taskprioritiser.repsoitory;

import com.example.taskprioritiser.service.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // TODO at what point is the task id generated

    // FYI – Using native queries purely as a reminder

    // where to add tbe persistnce context of the @transactional annotation?

    // TODO - improve table dynamics by creating a table for each score and connecting to task


    @Modifying
    @Query(value = "INSERT INTO task (task_id, desciption, effort, impact, urgency, deadline) " +
            "VALUES (:task_id, :desciption, :effort, :impact, :urgency, :deadline)", nativeQuery = true)
    void insertTask(@Param("task_id")int taskId, @Param("description")String description, @Param("effort")int effort, @Param("impact")int impact, @Param("urgency")int urgency, @Param("deadline")Instant deadline);

    @Modifying
    @Query(value = "UPDATE task (desciption, effort, impact, urgency, deadline) " +
            "VALUES (:desciption, :effort, :impact, :urgency, :deadline)" +
            "WHERE task_id = :task_id", nativeQuery = true)
    void updateTask(@Param("task_id")int taskId, @Param("description")String description, @Param("effort")int effort, @Param("impact")int impact, @Param("urgency")int urgency, @Param("deadline")Instant deadline);

    @Modifying
    @Query(value = "UPDATE task (desciption) " +
            "VALUES (:desciption)" +
            "WHERE task_id = :task_id", nativeQuery = true)
    void updateDescription(@Param("task_id")int taskId, @Param("description")String description);

    @Modifying
    @Query(value = "UPDATE task (effort, impact, urgency) " +
            "VALUES (:effort, :impact, :urgency)" +
            "WHERE task_id = :task_id", nativeQuery = true)
    void updateScores(@Param("task_id")int taskId, @Param("effort")int effort, @Param("impact")int impact, @Param("urgency")int urgency);

    @Modifying
    @Query(value = "UPDATE task (deadline) " +
            "VALUES (:deadline)" +
            "WHERE task_id = :task_id", nativeQuery = true)
    void updateDeadline(@Param("task_id")int taskId, @Param("deadline")Instant deadline);

    @Query(value = "SELECT * FROM task", nativeQuery = true)
    List<Task> fetchAllTasks();

    @Query(value = "SELECT * FROM task WHERE task_id = :id", nativeQuery = true)
    Task fetchTaskById(@Param("id") int taskID);
}
