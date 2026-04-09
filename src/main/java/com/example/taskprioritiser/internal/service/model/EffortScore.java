package com.example.taskprioritiser.internal.service.model;

import java.util.Map;

public class EffortScore extends Score {

    public EffortScore(int scoreValue) {
        super(ScoreType.EFFORT, scoreValue);
    }

    // the lower the effort the easier the relief
    // high effort tasks don't bring relief unless there is a dealine
    public int calculateAsSoleReliefFactor(Map<ScoreType, Integer> scoreWeights) {
        return (10 - this.getValue()) * scoreWeights.get(this.getType());
    }
}
