package com.example.taskprioritiser.controller;

import com.example.taskprioritiser.exposed.ScoreType;

//protected?
public class ExternalScoreTypeMapper {

    protected static com.example.taskprioritiser.service.ScoreType toService(ScoreType scoreType) {
        return switch (scoreType) {
            case EFFORT -> com.example.taskprioritiser.service.ScoreType.EFFORT;
            case IMPACT -> com.example.taskprioritiser.service.ScoreType.IMPACT;
            case URGENCY -> com.example.taskprioritiser.service.ScoreType.URGENCY;
        };
    }
}
