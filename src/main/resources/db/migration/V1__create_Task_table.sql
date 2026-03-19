CREATE TABLE task (
    task_id BIGINT PRIMARY KEY,
    description VARCHAR(255) UNIQUE NOT NULL,
    effort int NOT NULL,
    impact int NOT NULL,
    urgency int NOT NULL,
    deadline timestamp
);

-- scores to be in separate tables allowing them to be dynamic