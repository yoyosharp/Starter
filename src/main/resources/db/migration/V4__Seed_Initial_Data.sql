-- Insert initial roles
INSERT INTO mst_role (role_name, enabled_flag) VALUES
    ('ROLE_ADMIN', 1),
    ('ROLE_USER', 1),
    ('ROLE_GUEST', 1)
ON CONFLICT (role_name) DO NOTHING;

-- Insert initial permissions
WITH inserted_permissions AS (
    INSERT INTO mst_permission (permission_name, module_id, function_id, enabled_flag) VALUES
        ('User Management', 'USER', 'MANAGE', 1),
        ('User Read', 'USER', 'READ', 1),
        ('Role Management', 'ROLE', 'MANAGE', 1),
        ('Group Management', 'GROUP', 'MANAGE', 1)
    ON CONFLICT (module_id, function_id) DO NOTHING
    RETURNING id, module_id, function_id
)
-- Assign all permissions to admin role
INSERT INTO role_permission (role_id, permission_id, permission_level)
SELECT 
    (SELECT id FROM mst_role WHERE role_name = 'ROLE_ADMIN' LIMIT 1) as role_id,
    p.id as permission_id,
    2 as permission_level -- 2 = read_write
FROM inserted_permissions p
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Create initial admin user (password will be hashed by Spring Security)
-- Default password: 123456
WITH new_user AS (
    INSERT INTO mst_user (username, email, password, user_status)
    VALUES (
        'admin',
        'admin@example.com',
        '$2a$10$oMYG46b4Gv8Kqe4ZyrdZxeWKaHY9ZU4B6xhDCPy1qrT2GH2h3uQpm', -- 123456
        1
    )
    ON CONFLICT (username) DO NOTHING
    RETURNING id
)
-- Assign admin role to the user
INSERT INTO user_role (user_id, role_id)
SELECT 
    (SELECT id FROM new_user) as user_id,
    (SELECT id FROM mst_role WHERE role_name = 'ROLE_ADMIN' LIMIT 1) as role_id
WHERE EXISTS (SELECT 1 FROM new_user)
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Create a default group
INSERT INTO mst_group (group_name, enabled_flag)
VALUES ('Default Group', 1)
ON CONFLICT (group_name) DO NOTHING;
