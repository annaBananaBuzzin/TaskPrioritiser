package com.example.taskprioritiser.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
// Domain model
public class ScoreWeight {

    int id;
    String scoreName;
    @Setter
    int weight;

}
