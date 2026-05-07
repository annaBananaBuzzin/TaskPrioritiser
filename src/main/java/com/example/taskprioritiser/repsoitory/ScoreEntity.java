package com.example.taskprioritiser.repsoitory;

import com.example.taskprioritiser.service.model.ScoreType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="score_parameter")
@Deprecated
public class ScoreEntity {

    @Id
    private int id;

    @NotBlank
    @Column(nullable=false, unique=true)
    // should be an entity specific enum
    private ScoreType score;

    @Setter
    @Min(0)
    @Max(10)
    @Column(nullable=false)
    private int weight;

}
