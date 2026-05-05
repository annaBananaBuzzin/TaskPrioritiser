package com.example.taskprioritiser.api;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // or RANDOM_PORT
@AutoConfigureMockMvc
class TaskResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
//
//    @Autowired
//    private JsonMapper objectMapper;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

            // TODO - prepopulate the table so know task ID

    @Test
    void createTask_ShouldReturnCreatedTaskWithId() throws Exception {
        // Given
        TaskRequest request = new TaskRequest(
                "Complete project report",
                5,  // effort
                4,  // impact
                3,  // urgency
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        // When
        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.description").value("Complete project report"))
                .andExpect(jsonPath("$.description").value("Complete project report"))
                .andExpect(jsonPath("$.effort").value("5"))
                .andExpect(jsonPath("$.impact").value("4"))
                .andExpect(jsonPath("$.urgency").value("3"))
                .andExpect(jsonPath("$.deadline").value(Matchers.notNullValue()));
    }

    @Test
    void createTask_WithoutDeadline_ShouldReturnCreatedTask() throws Exception {
        // Given
        TaskRequest request = new TaskRequest(
                "Quick fix",
                2,  // effort
                3,  // impact
                5,  // urgency
                null  // no deadline
        );

        // When
        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.description").value("Quick fix"))
                .andExpect(jsonPath("$.effort").value("2"))
                .andExpect(jsonPath("$.impact").value("3"))
                .andExpect(jsonPath("$.urgency").value("5"))
                .andExpect(jsonPath("$.deadline").value(Matchers.nullValue()));
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() throws Exception {
        // Given - Create multiple tasks first
        TaskRequest task1 = new TaskRequest("Task 1", 5, 4, 3, Instant.now().plus(7, ChronoUnit.DAYS));
        TaskRequest task2 = new TaskRequest("Task 2", 3, 3, 3, null);

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task2)))
                .andExpect(status().isOk());

        // When
        mockMvc.perform(get("/task"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(Matchers.greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$[0].description").value(Matchers.notNullValue()));
    }

    @Test
    void getTask_ShouldReturnTaskById() throws Exception {
        // Given - Create a task first
        TaskRequest request = new TaskRequest("Get this task", 5, 4, 3, Instant.now().plus(7, ChronoUnit.DAYS));
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        // When
        mockMvc.perform(get("/task/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.description").value("Get this task"));
    }

    @Test
    void updateTask_ShouldUpdateDescription() throws Exception {
        // Given - Create a task
        TaskRequest request = new TaskRequest("Original description", 5, 4, 3, Instant.now().plus(7, ChronoUnit.DAYS));
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        // When
        UpdateDescriptionRequest updateDescriptionRequest = new UpdateDescriptionRequest("Updated description");
        mockMvc.perform(put("/task/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDescriptionRequest)))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/task/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void updateTask_ShouldUpdateDeadline() throws Exception {
        // Given - Create a task
        TaskRequest request = new TaskRequest("Task with deadline", 5, 4, 3, Instant.now().plus(7, ChronoUnit.DAYS));
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        // When - Update to a future deadline
        Instant futureDeadline = Instant.now().plus(14, ChronoUnit.DAYS);
        mockMvc.perform(put("/task/{id}/deadline", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(futureDeadline)))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/task/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deadline").value(Matchers.notNullValue()));
    }

    @Test
    void updateTask_WithPastDeadline_ShouldThrowException() throws Exception {
        // Given - Create a task
        TaskRequest request = new TaskRequest("Task with future deadline", 5, 4, 3, Instant.now().plus(7, ChronoUnit.DAYS));
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        // When - Try to update to a past deadline
        Instant pastDeadline = Instant.now().minus(1, ChronoUnit.DAYS);
        mockMvc.perform(put("/task/{id}/deadline", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pastDeadline)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_ShouldUpdateScore() throws Exception {
        // Given - Create a task
        TaskRequest request = new TaskRequest("Task with scores", 3, 2, 2, Instant.now().plus(7, ChronoUnit.DAYS));
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        // When - Update effort score
        mockMvc.perform(put("/task/{id}/{score}/{value}", taskId, ScoreType.EFFORT, 8))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/task/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.effort").value("8"));
    }

    @Test
    void fullWorkflow_ShouldCreateUpdateAndRetrieveTask() throws Exception {
        // Given - Create a task
        TaskRequest createRequest = new TaskRequest(
                "Full workflow task",
                5,  // effort
                4,  // impact
                3,  // urgency
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        // When - Create
        MvcResult createResult = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        Integer taskId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.taskId");

        // Update description
        UpdateDescriptionRequest updateDescriptionRequest = new UpdateDescriptionRequest("Updated workflow task");
        mockMvc.perform(put("/task/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDescriptionRequest)))
                .andExpect(status().isOk());

        // Update effort score
        mockMvc.perform(put("/task/{id}/{score}/{value}", taskId, ScoreType.EFFORT, 8))
                .andExpect(status().isOk());

        // Update impact score
        mockMvc.perform(put("/task/{id}/{score}/{value}", taskId, ScoreType.IMPACT, 7))
                .andExpect(status().isOk());

        // Update deadline
        Instant newDeadline = Instant.now().plus(14, ChronoUnit.DAYS);
        mockMvc.perform(put("/task/{id}/deadline", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDeadline)))
                .andExpect(status().isOk());

        // Then - Retrieve final state
        mockMvc.perform(get("/task/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.description").value("Updated workflow task"))
                .andExpect(jsonPath("$.effort").value("8"))
                .andExpect(jsonPath("$.impact").value("7"))
                .andExpect(jsonPath("$.urgency").value("3"))
                .andExpect(jsonPath("$.deadline").value(Matchers.notNullValue()));
    }

}
