CREATE TABLE score_parameter(
    id BIGINT PRIMARY KEY,
    scoreWeight VARCHAR(255) UNIQUE NOT NULL,
    weight int NOT NULL,
);

INSERT INTO scoreWeight (id, scoreWeight, weight) VALUES (1, 'effort', 3);
INSERT INTO scoreWeight (id, scoreWeight, weight) VALUES (2, 'impact', 2);
INSERT INTO scoreWeight (id, scoreWeight, weight) VALUES (3, 'urgency', 3);
