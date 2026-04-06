package com.example.taskprioritiser;

import com.example.taskprioritiser.internal.service.model.ScoreType;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Map;

@ConfigurationProperties(prefix = "prioritiser")
@ConfigurationPropertiesScan
@Getter
public class PrioritiserConfig {

    private int dayDeadlineConstant;
    private int scoreMaxValue;
    private Map<ScoreType, Integer> scoreWeightMap;
}
