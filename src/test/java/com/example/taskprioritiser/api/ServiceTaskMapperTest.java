package com.example.taskprioritiser.api;

import com.example.taskprioritiser.service.model.*;
import com.example.taskprioritiser.service.model.ScoreType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTaskMapperTest {

    @ParameterizedTest
    @ValueSource(ints = {-4, 0, 11})
    void fromCreationRequest_WithInvalidEffortScore_ShouldThrow(int value) {
        // With
        TaskRequest request = TestTaskRequestBuilder.create().withEffortScore(value).build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ServiceTaskMapper.fromCreationRequest(request));
        assertEquals(ScoreType.EFFORT + " score value must be between 1 and 10", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {-4, 0, 11})
    void fromCreationRequest_WithInvalidImpactScore_ShouldThrow(int value) {
        // With
        TaskRequest request = TestTaskRequestBuilder.create().withImpactScore(value).build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ServiceTaskMapper.fromCreationRequest(request));
        assertEquals(ScoreType.IMPACT + " score value must be between 1 and 10", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {-4, 0, 11})
    void fromCreationRequest_WithInvalidUrgencyScore_ShouldThrow(int value) {
        // With
        TaskRequest request = TestTaskRequestBuilder.create().withUrgencyScore(value).build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ServiceTaskMapper.fromCreationRequest(request));
        assertEquals(ScoreType.URGENCY + " score value must be between 1 and 10", exception.getMessage());
    }
}