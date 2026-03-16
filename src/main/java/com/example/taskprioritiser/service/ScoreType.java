package com.example.taskprioritiser.service;

public enum ScoreType {
    EFFORT, IMPACT, URGENCY;

    public Score create(int value) {
        return switch (this) {
            case EFFORT -> new EffortScore(value);
            case IMPACT -> new ImpactScore(value);
            case URGENCY -> new UrgencyScore(value);
        };
    }
}
