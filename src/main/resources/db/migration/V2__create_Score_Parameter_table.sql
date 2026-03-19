CREATE TABLE score_parameter(
    id BIGINT PRIMARY KEY,
    score VARCHAR(255) UNIQUE NOT NULL,
    weight int NOT NULL
);

INSERT INTO score_parameter (id, score, weight) VALUES (1, 'effort', 3);
INSERT INTO score_parameter (id, score, weight) VALUES (2, 'impact', 2);
INSERT INTO score_parameter (id, score, weight) VALUES (3, 'urgency', 3);
