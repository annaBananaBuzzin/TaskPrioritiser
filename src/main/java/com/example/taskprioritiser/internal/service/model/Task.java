package com.example.taskprioritiser.internal.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class Task {

    // TODO - getter can throw null pointer exception if this is a new task without id
    // this is only applicabel for entity task .. a service task should always have an id
    // just don't have a get method for id on entity.. but how to map to service? directly reference the field
    private final Long taskId;
    private String description;
    // would this be a list o
    private EffortScore effortScore;
    private ImpactScore impactScore;
    private UrgencyScore urgencyScore;
    // this can be null
    private Instant deadline;

    // add get methods for each score value
    //    public Map<ScoreType, Integer> getScoreValueMap() {
    //        Map<ScoreType, Integer> scoreMap = new HashMap<>();
    //        scoreMap.put(ScoreType.EFFORT, effortScore.getValue());
    //        scoreMap.put(ScoreType.IMPACT, impactScore.getValue());
    //        scoreMap.put(ScoreType.URGENCY, urgencyScore.getValue());
    //        return scoreMap;
    //    }

}
