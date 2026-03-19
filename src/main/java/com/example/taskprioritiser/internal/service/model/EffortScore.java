package com.example.taskprioritiser.internal.service.model;

public class EffortScore extends Score {

    public EffortScore(int scoreValue) {
        super(ScoreType.EFFORT, scoreValue);
    }

//    @Override
//    public int calculateWeightedScore(ScoreWeight weight) {
//        return this.getScoreValue() * weight.getWeight();
//    }
}
