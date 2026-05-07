package com.example.taskprioritiser.api;

import com.example.taskprioritiser.api.dto.ScoreType;
import com.example.taskprioritiser.api.dto.TaskRequest;
import com.example.taskprioritiser.api.dto.UpdateDescriptionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class TaskResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

// Still unsure why it isn't getting this bean
//    @Autowired
//    private JsonMapper objectMapper;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // TODO - prepopulate the table so know task ID

    @Test
    void createTask_ShouldReturnCreatedTaskWithId() throws Exception {
        // Given
        TaskRequest request = TestTaskRequestBuilder.create()
                .withDescription("Complete project report")
                .build();

        // When & Then
        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.taskId").value(Matchers.greaterThan(0)),
                        jsonPath("$.description").value("Complete project report"),
                        jsonPath("$.effort").value("5"),
                        jsonPath("$.impact").value("4"),
                        jsonPath("$.urgency").value("3"),
                        jsonPath("$.deadline").value(Matchers.notNullValue()));
    }

    @Test
    void createTask_WithoutDeadline_ShouldReturnCreatedTask() throws Exception {
        // Given
        TaskRequest request = TestTaskRequestBuilder.create()
                .withDescription("Quick fix")
                .withDeadline(null)
                .build();

        // When & Then
        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.taskId").value(Matchers.greaterThan(0)),
                        jsonPath("$.description").value("Quick fix"),
                        jsonPath("$.effort").value("5"),
                        jsonPath("$.impact").value("4"),
                        jsonPath("$.urgency").value("3"),
                        jsonPath("$.deadline").value(Matchers.nullValue()));
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() throws Exception {
        // Given
        TaskRequest task1 = TestTaskRequestBuilder.create()
                .withDescription("Task 1")
                .build();
        TaskRequest task2 = TestTaskRequestBuilder.create()
                .withDescription("Task 2")
                .withDeadline(null)
                .build();

// When & Then
        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task2)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/task"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$.length()").value(Matchers.greaterThanOrEqualTo(2)),
                        jsonPath("$[0].description").value(Matchers.notNullValue()));
    }

    @Test
    void getTask_ShouldReturnTaskById() throws Exception {
        // Given
        TaskRequest request = TestTaskRequestBuilder.create()
                .withDescription("Get this task")
                .build();

// When & Then
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        mockMvc.perform(get("/task/{id}", taskId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.taskId").value(taskId),
                        jsonPath("$.description").value("Get this task"));
    }

    @Test
    void updateTask_ShouldUpdateDescription() throws Exception {
        // Given
        TaskRequest request = TestTaskRequestBuilder.create()
                .withDescription("Original description")
                .build();

// When & Then
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        UpdateDescriptionRequest updateDescriptionRequest = new UpdateDescriptionRequest("Updated description");
        mockMvc.perform(put("/task/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDescriptionRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/task/{id}", taskId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.description").value("Updated description"));
    }

    @Test
    void updateTask_ShouldUpdateDeadline() throws Exception {
        // Given
        TaskRequest request = TestTaskRequestBuilder.create()
                .withDescription("Task with deadline")
                .build();
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        Instant futureDeadline = Instant.now().plus(14, ChronoUnit.DAYS);
        mockMvc.perform(put("/task/{id}/deadline", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(futureDeadline)))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/task/{id}", taskId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.deadline").value(Matchers.notNullValue()));
    }

    @Test
    void updateTask_WithPastDeadline_ShouldThrowException() throws Exception {
        // Given
        TaskRequest request = TestTaskRequestBuilder.create()
                .withDescription("Task with future deadline")
                .build();
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        Instant pastDeadline = Instant.now().minus(1, ChronoUnit.DAYS);
        mockMvc.perform(put("/task/{id}/deadline", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pastDeadline)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_ShouldUpdateScore() throws Exception {
        // Given
        TaskRequest request = TestTaskRequestBuilder.create()
                .withDescription("Task with scores")
                .withEffortScore(5)
                .build();
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        mockMvc.perform(put("/task/{id}/{score}/{value}", taskId, ScoreType.EFFORT, 8))
                .andExpect(status().isOk());

        mockMvc.perform(get("/task/{id}", taskId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.effort").value("8"));
    }

    @Test
    void updateTask_WithInvalidScore_ShouldThrowException() throws Exception {
        TaskRequest request = TestTaskRequestBuilder.create()
                .withDescription("Task with other scores")
                .withEffortScore(5)
                .build();
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        mockMvc.perform(put("/task/{id}/{score}/{value}", taskId, ScoreType.IMPACT, 12))
                .andExpect(status().isBadRequest());

    }

    @Test
    void deleteTask_ShouldDeleteTask() throws Exception {
        // Given
        TaskRequest request = TestTaskRequestBuilder.create()
                .withDescription("Task to be deleted")
                .build();
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        mockMvc.perform(delete("/task/{id}", taskId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/task/{id}", taskId)).andExpect(status().isNotFound());

    }

    @Test
    void deleteTask_WithNotFoundTaskId_ShouldDoNothing() throws Exception {
        Random random = new Random();
        Long idCandidate;

        do {
            idCandidate = random.nextLong(1, Long.MAX_VALUE);
        } while (mockMvc.perform(get("/task/{id}", idCandidate)).andReturn().getResponse().getStatus() == 200);

        mockMvc.perform(delete("/task/{id}", idCandidate))
                .andExpect(status().isOk());

    }

    @Test
    void fullWorkflow_ShouldCreateUpdateAndRetrieveTask() throws Exception {
        TaskRequest request = TestTaskRequestBuilder.create()
                .withDescription("Full workflow task")
                .build();

        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        UpdateDescriptionRequest updateDescriptionRequest = new UpdateDescriptionRequest("Updated workflow task");
        mockMvc.perform(put("/task/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDescriptionRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/task/{id}/{score}/{value}", taskId, ScoreType.EFFORT, 8))
                .andExpect(status().isOk());

        mockMvc.perform(put("/task/{id}/{score}/{value}", taskId, ScoreType.IMPACT, 7))
                .andExpect(status().isOk());

        Instant newDeadline = Instant.now().plus(14, ChronoUnit.DAYS);
        mockMvc.perform(put("/task/{id}/deadline", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDeadline)))
                .andExpect(status().isOk());

        // Then - Retrieve final state
        mockMvc.perform(get("/task/{id}", taskId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.taskId").value(taskId),
                        jsonPath("$.description").value("Updated workflow task"),
                        jsonPath("$.effort").value("8"),
                        jsonPath("$.impact").value("7"),
                        jsonPath("$.urgency").value("3"),
                        jsonPath("$.deadline").value(Matchers.notNullValue()));
    }
}

