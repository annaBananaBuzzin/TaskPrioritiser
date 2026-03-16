package com.example.taskprioritiser.service;

public abstract class Score {

    ScoreType type;
    int value;

     public Score(ScoreType scoreType, int scoreValue) {
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
        this.value = value;
    }


//     public abstract int calculateWeightedScore(ScoreWeight weight);
}
