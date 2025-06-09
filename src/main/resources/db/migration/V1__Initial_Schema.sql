-- Create user table
CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    password VARCHAR(60) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    verified_at TIMESTAMP,
    status INT DEFAULT 1
);

-- Create role table
CREATE TABLE IF NOT EXISTS "role" (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    name VARCHAR(100) NOT NULL UNIQUE,
    enabled_flag INT DEFAULT 1
);

-- Create permission table
CREATE TABLE IF NOT EXISTS "permission" (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    name VARCHAR(100) NOT NULL,
    module_id VARCHAR(100) NOT NULL,
    function_id VARCHAR(100) NOT NULL,
    enabled_flag INT DEFAULT 1,
    CONSTRAINT uk_permission_module_function UNIQUE (module_id, function_id)
);

-- Create group table (using quotes as 'group' is a reserved word in SQL)
CREATE TABLE IF NOT EXISTS "group" (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    name VARCHAR(100) NOT NULL UNIQUE,
    enabled_flag INT DEFAULT 1
);

-- Create user_role join table
CREATE TABLE IF NOT EXISTS "user_role" (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
);

-- Create role_permission join table
CREATE TABLE IF NOT EXISTS "role_permission" (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    level INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
);

-- Create group_user join table
CREATE TABLE IF NOT EXISTS "group_user" (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    CONSTRAINT uk_group_user UNIQUE (user_id, group_id)
);

-- Create group_permission join table
CREATE TABLE IF NOT EXISTS "group_permission" (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    group_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    level INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_group_permission UNIQUE (group_id, permission_id)
);

-- Create user_permission join table
CREATE TABLE IF NOT EXISTS "user_permission" (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    user_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    level INT DEFAULT 0,
    CONSTRAINT uk_user_permission UNIQUE (user_id, permission_id)
);
