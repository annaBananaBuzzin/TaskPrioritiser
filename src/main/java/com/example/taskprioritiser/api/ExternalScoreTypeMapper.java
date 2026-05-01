package com.example.taskprioritiser.api;

//protected?
public class ExternalScoreTypeMapper {

    protected static com.example.taskprioritiser.service.model.ScoreType toService(ScoreType scoreType) {
        return switch (scoreType) {
            case EFFORT -> com.example.taskprioritiser.service.model.ScoreType.EFFORT;
            case IMPACT -> com.example.taskprioritiser.service.model.ScoreType.IMPACT;
            case URGENCY -> com.example.taskprioritiser.service.model.ScoreType.URGENCY;
        };
    }
}
