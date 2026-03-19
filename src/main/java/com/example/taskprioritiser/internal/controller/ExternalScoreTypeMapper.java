package com.example.taskprioritiser.internal.controller;

import com.example.taskprioritiser.api.ScoreType;

//protected?
public class ExternalScoreTypeMapper {

    protected static com.example.taskprioritiser.internal.service.model.ScoreType toService(ScoreType scoreType) {
        return switch (scoreType) {
            case EFFORT -> com.example.taskprioritiser.internal.service.model.ScoreType.EFFORT;
            case IMPACT -> com.example.taskprioritiser.internal.service.model.ScoreType.IMPACT;
            case URGENCY -> com.example.taskprioritiser.internal.service.model.ScoreType.URGENCY;
        };
    }
}
