CREATE OR REPLACE FUNCTION get_user_auth(identity TEXT)
RETURNS TABLE (
    user_id BIGINT,
    username TEXT,
    status INT,
    verified_at TIMESTAMP,
    module_id TEXT,
    function_id TEXT,
    level INT,
    enabled_flag INT
) AS $$
BEGIN
RETURN QUERY
    WITH user_base AS (
        SELECT u.id, u.username, u.status, u.verified_at
        FROM "user" u
        WHERE u.username = identity OR u.email = identity
        LIMIT 1
    ),
    user_perms AS (
        SELECT p.module_id, p.function_id, up.level, p.enabled_flag
        FROM user_permission up
        JOIN permission p ON p.id = up.permission_id
        JOIN user_base ub ON ub.id = up.user_id
    ),
    role_perms AS (
        SELECT p.module_id, p.function_id, rp.level, p.enabled_flag
        FROM user_base ub
        JOIN user_role ur ON ur.user_id = ub.id
        JOIN "role" r ON r.id = ur.role_id AND r.enabled_flag = 1
        JOIN role_permission rp ON rp.role_id = r.id
        JOIN permission p ON p.id = rp.permission_id
    ),
    group_perms AS (
        SELECT p.module_id, p.function_id, gp.level, p.enabled_flag
        FROM user_base ub
        JOIN group_user gu ON gu.user_id = ub.id
        JOIN "group" g ON g.id = gu.group_id
        JOIN group_permission gp ON gp.group_id = g.id
        JOIN permission p ON p.id = gp.permission_id
    ),
    all_role_group_perms AS (
        SELECT * FROM role_perms
        UNION ALL
        SELECT * FROM group_perms
    ),
    resolved_role_group_perms AS (
        SELECT DISTINCT ON (module_id, function_id)
            module_id, function_id, level, enabled_flag
        FROM all_role_group_perms
        ORDER BY module_id, function_id, level DESC
    ),
    all_permissions AS (
        SELECT * FROM user_perms
        UNION ALL
        SELECT * FROM resolved_role_group_perms
    ),
    effective_permissions AS (
        SELECT DISTINCT ON (module_id, function_id)
            module_id, function_id, level, enabled_flag
        FROM all_permissions
        ORDER BY module_id, function_id,
            CASE
                WHEN EXISTS (
                    SELECT 1 FROM user_perms up
                    WHERE up.module_id = all_permissions.module_id
                    AND up.function_id = all_permissions.function_id
                ) THEN 1
                ELSE 2
            END,
            level DESC
    )
SELECT
    ub.id, ub.username, ub.status, ub.verified_at,
    ep.module_id, ep.function_id, ep.level, ep.enabled_flag
FROM user_base ub
         JOIN effective_permissions ep ON TRUE;
END;
$$ LANGUAGE plpgsql;

GRANT EXECUTE ON FUNCTION get_user_auth(TEXT) TO baseappadmin;
