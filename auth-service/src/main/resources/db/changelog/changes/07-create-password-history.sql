CREATE TABLE IF NOT EXISTS password_history (
                                                id BIGSERIAL PRIMARY KEY,
                                                user_id BIGINT NOT NULL,
                                                password VARCHAR(255) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
CREATE INDEX IF NOT EXISTS idx_history_user ON password_history(user_id);