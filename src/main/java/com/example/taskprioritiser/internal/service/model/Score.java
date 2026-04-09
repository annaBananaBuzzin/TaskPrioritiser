package com.example.taskprioritiser.internal.service.model;

import java.util.Map;

public abstract class Score {

    ScoreType type;
    int value;

    public Score(ScoreType scoreType, int scoreValue) {
        validateValue(scoreValue);
        this.type = scoreType;
        this.value = scoreValue;
    }

    public ScoreType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        validateValue(value);
        this.value = value;
    }

    private void validateValue(int value) {
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException("Score value must be between 1 and 10");
        }
    }

    public int calculateWeightedScore(Map<ScoreType, Integer> scoreWeights) {
        return this.getValue() * scoreWeights.get(this.type);
    }
}
