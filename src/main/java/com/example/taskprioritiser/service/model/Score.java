package com.example.taskprioritiser.service.model;

import java.util.Map;

public abstract class Score {

    ScoreType type;
    int value;

    public Score(ScoreType scoreType, int scoreValue) {
        this.type = scoreType;
        setValue(scoreValue);
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

    // should the validation of this score live here? It is fundamental to the Score class but is it a business logic concern instead?
    private void validateValue(int value) {
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException(type + " score value must be between 1 and 10");
        }

    }

    public int calculateWeightedScore(Map<ScoreType, Integer> scoreWeights) {
        return this.getValue() * scoreWeights.get(this.type);
    }
}
