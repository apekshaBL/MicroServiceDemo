CREATE TABLE IF NOT EXISTS user_profiles (
                                             id BIGSERIAL PRIMARY KEY,
                                             username VARCHAR(255) NOT NULL UNIQUE, -- Add this!
    user_id BIGINT NOT NULL UNIQUE,        -- Keep this for the link to auth-service
    full_name VARCHAR(100),
    bio TEXT,
    profile_picture_url VARCHAR(255),
    phone_number VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );