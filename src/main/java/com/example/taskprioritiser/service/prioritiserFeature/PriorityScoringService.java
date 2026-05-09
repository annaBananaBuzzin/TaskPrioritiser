package com.example.taskprioritiser.service.prioritiserFeature;

import com.example.taskprioritiser.PrioritiserConfig;
import com.example.taskprioritiser.service.model.ScoreType;
import com.example.taskprioritiser.service.model.Task;
import com.example.taskprioritiser.service.model.TaskPriority;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Map;

@Service
public class PriorityScoringService {

    private final DeadlinePropertiesService deadlinePropertiesService;

    public PriorityScoringService(DeadlinePropertiesService deadlinePropertiesService) {
        this.deadlinePropertiesService = deadlinePropertiesService;
    }

    public TaskPriority getTaskPriority(Task task, LocalDateTime now) {

        Map<ScoreType, Integer> scoreWeights = PrioritiserConfig.getScoreWeightMap();

        int benefitScore = calculateBenefitScore(task, scoreWeights);

        Instant deadline = task.getDeadline();
        if (deadline == null) {
            // When no deadline set, effort is the only relief factor
            int reliefScore = task.getEffortScore().calculateAsSoleReliefFactor(scoreWeights);
            return new TaskPriority(task, benefitScore + reliefScore);
        }

        DeadlineProperties deadlineProperties = deadlinePropertiesService.getDeadlineProperties(deadline, now);
        // relief includes quick wins and overdue tasks that are not being prioritised
        double reliefScore = task.getEffortScore().calculateWeightedScore(scoreWeights) / deadlinePropertiesService.getDeadlineVariable(deadlineProperties);

        return new TaskPriority(task, deadlineProperties.isToday(), benefitScore + reliefScore);
    }

    private int calculateBenefitScore(Task task, Map<ScoreType, Integer> scoreWeights) {
        int impactWeightedScore = task.getImpactScore().calculateWeightedScore(scoreWeights);
        int urgencyWeightedScore = task.getUrgencyScore().calculateWeightedScore(scoreWeights);
        return impactWeightedScore + urgencyWeightedScore;
    }

}
