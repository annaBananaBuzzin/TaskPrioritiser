CREATE TABLE TASK (
    task_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255) NOT NULL,
    effort int NOT NULL,
    impact int NOT NULL,
    urgency int NOT NULL,
    deadline timestamp,
);