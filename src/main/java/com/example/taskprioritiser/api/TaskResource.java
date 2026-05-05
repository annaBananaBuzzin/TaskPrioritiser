package com.example.taskprioritiser.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@RequestMapping("/task")
public interface TaskResource {

    @PostMapping
    @ResponseBody
    TaskResponse createTask(@Valid @RequestBody TaskRequest taskRequest);

    @GetMapping
    @ResponseBody
    List<TaskResponse> getAllTasks();

    @GetMapping("/{id}")
    @ResponseBody
    TaskResponse getTask(@PathVariable Long id);

    @PutMapping("/{id}")
    void updateTask(@PathVariable Long id, @RequestBody UpdateDescriptionRequest newDescription);

    @PutMapping("/{id}/deadline")
    void updateTask(@PathVariable Long id, @RequestBody Instant taskDeadline);

    @PutMapping("/{id}/{score}/{value}")
    void updateTask(@PathVariable Long id, @PathVariable ScoreType score, @PathVariable int value);

    @DeleteMapping("/{id}")
    void deleteTask(@PathVariable Long id);

    @ExceptionHandler(value = NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleNoSuchElementException(NoSuchElementException ex);

    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex);

}
