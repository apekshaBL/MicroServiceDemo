-- Liquibase formatted SQL
-- changeset author:auth_service_dev id:1

CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_name VARCHAR(20) DEFAULT 'ROLE_USER',
    is_active BOOLEAN DEFAULT TRUE,
    reset_token VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Index for Sign-in check and Forgot Password use cases
CREATE INDEX idx_users_email ON users(email);