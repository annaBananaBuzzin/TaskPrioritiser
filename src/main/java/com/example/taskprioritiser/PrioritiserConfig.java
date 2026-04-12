package com.example.taskprioritiser;

import com.example.taskprioritiser.internal.service.model.ScoreType;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Map;

@ConfigurationProperties(prefix = "prioritiser")
@ConfigurationPropertiesScan
public class PrioritiserConfig {

    @Getter
    public static int dayDeadlineConstant;
    @Getter
    public static Map<ScoreType, Integer> scoreWeightMap;

}
