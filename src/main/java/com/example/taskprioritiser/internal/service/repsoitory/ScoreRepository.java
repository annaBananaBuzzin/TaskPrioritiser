package com.example.taskprioritiser.internal.service.repsoitory;

import com.example.taskprioritiser.internal.service.repsoitory.entity.ScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Deprecated
public interface ScoreRepository extends JpaRepository<ScoreEntity, Long> {

    // FYI – Using native queries purely as a reminder

    // TODO - improve table dynamics by creating a table for each score and connecting to task

    @Modifying
    @Query(value = "UPDATE score_weight (weight) " +
            "VALUES (:weight)" +
            "WHERE score_type = :score_type", nativeQuery = true)
    void updateScoreWeight(@Param("score_type")String scoreType, @Param("weight")int weight);

    @Query(value = "SELECT * FROM score_weight", nativeQuery = true)
    List<ScoreEntity> fetchAllScores();

    @Query(value = "SELECT * FROM score_weight WHERE type = :scoreType", nativeQuery = true)
    ScoreEntity fetchScore(@Param("type")String scoreType);
}
