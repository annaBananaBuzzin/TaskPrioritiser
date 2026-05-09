package com.example.taskprioritiser.api;

import com.example.taskprioritiser.api.dto.TaskResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping("/prioritise")
public interface PrioritiserResource {

    @GetMapping
    @ResponseBody
    List<TaskResponse> getAllTasks();

    @GetMapping
    @ResponseBody
    List<TaskResponse> getAllOverdueTasks();

    @GetMapping
    @ResponseBody
    List<TaskResponse> getAllOutstandingTasksPrioritised();

    @GetMapping
    @ResponseBody
    List<TaskResponse> getAllOutstandingTasksDueTodayPrioritised();

}
