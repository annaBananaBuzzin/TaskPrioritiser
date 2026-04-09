package com.example.taskprioritiser;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

@Configuration
@ConfigurationPropertiesScan
@Getter
@Deprecated
public class ServiceConfig {

    @Value("${app.service.timezone}")
    private String timezone;
    private final ZoneId zoneId;

    public ServiceConfig() {
        this.zoneId = ZoneId.of(timezone);
    }
}
