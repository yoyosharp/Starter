-- Create user table
CREATE TABLE IF NOT EXISTS mst_user (
    id BIGSERIAL PRIMARY KEY,
    password VARCHAR(60) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    verified_at TIMESTAMP,
    user_status INT2 DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create role table
CREATE TABLE IF NOT EXISTS mst_role (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE,
    enabled_flag int2 DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create permission table
CREATE TABLE IF NOT EXISTS mst_permission (
    id BIGSERIAL PRIMARY KEY,
    permission_name VARCHAR(100) NOT NULL,
    module_id VARCHAR(100) NOT NULL,
    function_id VARCHAR(100) NOT NULL,
    enabled_flag INT2 DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_permission_module_function UNIQUE (module_id, function_id)
);

-- Create group table
CREATE TABLE IF NOT EXISTS mst_group (
    id BIGSERIAL PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL UNIQUE,
    enabled_flag INT2 DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user_role join table
CREATE TABLE IF NOT EXISTS "user_role" (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES mst_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES mst_role (id) ON DELETE CASCADE
);

-- Create role_permission join table
CREATE TABLE IF NOT EXISTS "role_permission" (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    permission_level INT2 NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
);

-- Create group_user join table
CREATE TABLE IF NOT EXISTS "group_user" (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_group_user UNIQUE (user_id, group_id),
    CONSTRAINT fk_group_user_user FOREIGN KEY (user_id) REFERENCES mst_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_group_permission_group FOREIGN KEY (group_id) REFERENCES mst_group (id) ON DELETE CASCADE
);

-- Create group_permission join table
CREATE TABLE IF NOT EXISTS "group_permission" (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    permission_level INT2 NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_group_permission UNIQUE (group_id, permission_id)
);

-- Create user_permission join table
CREATE TABLE IF NOT EXISTS "user_permission" (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    permission_level INT2 DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_permission UNIQUE (user_id, permission_id),
    CONSTRAINT fk_user_permission_user FOREIGN KEY (user_id) REFERENCES mst_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_permission_permission FOREIGN KEY (permission_id) REFERENCES mst_permission (id) ON DELETE CASCADE
);

-- Create refresh_token table
CREATE TABLE IF NOT EXISTS refresh_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expiration_time TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uc_refresh_token_user_id UNIQUE (user_id)
);

-- Create index on user_id for faster lookups
CREATE INDEX IF NOT EXISTS idx_refresh_token_user_id ON refresh_token (user_id);
