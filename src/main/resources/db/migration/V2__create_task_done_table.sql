CREATE TABLE task_done (
    task_done_id BIGINT PRIMARY KEY,
    done_at timestamp,
    task_id BIGINT,
    CONSTRAINT fk_task_id FOREIGN KEY (task_id) REFERENCES task(task_id)
);
