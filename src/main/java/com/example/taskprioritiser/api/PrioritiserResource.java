package com.example.taskprioritiser.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/prioritise")
public interface PrioritiserResource {

        @GetMapping
        List<TaskResponse> getAllTasksPrioritised();
}
