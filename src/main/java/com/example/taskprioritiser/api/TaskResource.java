package com.example.taskprioritiser.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

// in external package
// package with the DTOs that can be exposed to the outside world
// prevents mistakes of exposing internal domain models
@RequestMapping("/task")
public interface TaskResource {

    @PostMapping
    TaskResponse createTask(@Valid @RequestBody TaskRequest taskRequest);

    @GetMapping
    List<TaskResponse> getAllTasks();

    @GetMapping("/{id}")
    TaskResponse getTask(@PathVariable Long id);

//    @PutMapping("/{id}")
//    // should this be different dto?
//    void updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest);

    @PutMapping("/{id}")
    void updateTask(@PathVariable Long id, @RequestBody String description);

    @PutMapping("/{id}/deadline")
    void updateTask(@PathVariable Long id, @RequestBody Instant taskDeadline);

    @PutMapping("/{id}/{score}/{value}")
    void updateTask(@PathVariable Long id, @PathVariable ScoreType score, @PathVariable int value);

}
