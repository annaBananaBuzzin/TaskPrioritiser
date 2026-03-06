package com.example.taskprioritiser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskPrioritiserApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TaskPrioritiserApplication.class);
        app.setWebApplicationType(WebApplicationType.SERVLET); // <--- add this line
        app.run(args);
    }

}
