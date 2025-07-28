-- Create table for OAuth2 provider information
CREATE TABLE user_oauth2_provider (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider_name VARCHAR(50) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    provider_email VARCHAR(255),
    provider_attributes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    CONSTRAINT fk_oauth2_provider_user FOREIGN KEY (user_id) REFERENCES mst_user(id) ON DELETE CASCADE,
    CONSTRAINT uk_oauth2_provider UNIQUE (provider_id, provider_name)
);

-- Create index for faster lookups
CREATE INDEX idx_oauth2_provider_user_id ON user_oauth2_provider(user_id);
CREATE INDEX idx_oauth2_provider_email ON user_oauth2_provider(provider_email);

-- Add comment to table
COMMENT ON TABLE user_oauth2_provider IS 'Stores OAuth2 provider information for users';