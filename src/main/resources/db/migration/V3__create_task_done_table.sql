CREATE TABLE task_done (
    task_done_id BIGINT PRIMARY KEY,
    done_at timestamp NOT NULL,
    task_id BIGINT NOT NULL,
    FOREIGN KEY (task_id) REFERENCES task(task_id)
);
