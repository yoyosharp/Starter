-- Insert initial roles
INSERT INTO "role" (name, enabled_flag, created_at, updated_at) VALUES
    ('ROLE_ADMIN', 1, NOW(), NOW()),
    ('ROLE_USER', 1, NOW(), NOW()),
    ('ROLE_GUEST', 1, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- Insert initial permissions
WITH inserted_permissions AS (
    INSERT INTO "permission" (name, module_id, function_id, enabled_flag, created_at, updated_at) VALUES
        ('User Management', 'USER', 'MANAGE', 1, NOW(), NOW()),
        ('User Read', 'USER', 'READ', 1, NOW(), NOW()),
        ('Role Management', 'ROLE', 'MANAGE', 1, NOW(), NOW()),
        ('Group Management', 'GROUP', 'MANAGE', 1, NOW(), NOW())
    ON CONFLICT (module_id, function_id) DO NOTHING
    RETURNING id, module_id, function_id
)
-- Assign all permissions to admin role
INSERT INTO role_permission (role_id, permission_id, level, created_at, updated_at)
SELECT 
    (SELECT id FROM "role" WHERE name = 'ROLE_ADMIN' LIMIT 1) as role_id,
    p.id as permission_id,
    2 as level, -- 2 = read_write
    NOW() as created_at,
    NOW() as updated_at
FROM inserted_permissions p
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Create initial admin user (password will be hashed by Spring Security)
-- Default password: admin123
WITH new_user AS (
    INSERT INTO "user" (username, email, password, status, created_at, updated_at)
    VALUES (
        'admin',
        'admin@example.com',
        '$2a$10$XptfskLsT1SL/bOzZLkJM.3tfbg/2CQDszCQbJ8Bwx0A5MO/UzQaO', -- admin123
        1,
        NOW(),
        NOW()
    )
    ON CONFLICT (username) DO NOTHING
    RETURNING id
)
-- Assign admin role to the user
INSERT INTO user_role (user_id, role_id, created_at, updated_at)
SELECT 
    (SELECT id FROM new_user) as user_id,
    (SELECT id FROM "role" WHERE name = 'ROLE_ADMIN' LIMIT 1) as role_id,
    NOW() as created_at,
    NOW() as updated_at
WHERE EXISTS (SELECT 1 FROM new_user)
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Create a default group
INSERT INTO "group" (name, enabled_flag, created_at, updated_at)
VALUES ('Default Group', 1, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
