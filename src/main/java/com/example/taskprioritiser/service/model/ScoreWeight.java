package com.example.taskprioritiser.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Deprecated
 public class ScoreWeight {

    int id;
    String scoreName;
    @Setter
    int weight;

}
