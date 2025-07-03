CREATE OR REPLACE FUNCTION get_user_auth(identity text)
RETURNS TABLE(
    user_id bigint,
    username varchar,
    user_status int2,
    verified_at timestamp without time zone,
    module_id varchar,
    function_id varchar,
    permission_name varchar,
    permission_level int2,
    enabled_flag int2
)
LANGUAGE plpgsql
AS
$$
BEGIN
RETURN QUERY
    WITH user_base AS (
        SELECT u.id, u.username, u.user_status, u.verified_at
        FROM mst_user u
        WHERE u.username = identity
        LIMIT 1
    ),
    user_perms AS (
        SELECT
            p.module_id AS up_module_id,
            p.function_id AS up_function_id,
            up.permission_level AS up_permission_level,
            p.enabled_flag AS up_enabled_flag
        FROM user_permission up
        JOIN mst_permission p ON p.id = up.permission_id
        JOIN user_base ub ON ub.id = up.user_id
    ),
    role_perms AS (
        SELECT
            p.module_id AS rp_module_id,
            p.function_id AS rp_function_id,
            rp.permission_level AS rp_permission_level,
            p.enabled_flag AS rp_enabled_flag
        FROM user_base ub
        JOIN user_role ur ON ur.user_id = ub.id
        JOIN mst_role r ON r.id = ur.role_id AND r.enabled_flag = 1
        JOIN role_permission rp ON rp.role_id = r.id
        JOIN mst_permission p ON p.id = rp.permission_id
    ),
    group_perms AS (
        SELECT
            p.module_id AS gp_module_id,
            p.function_id AS gp_function_id,
            gp.permission_level AS gp_permission_level,
            p.enabled_flag AS gp_enabled_flag
        FROM user_base ub
        JOIN group_user gu ON gu.user_id = ub.id
        JOIN mst_group g ON g.id = gu.group_id AND g.enabled_flag = 1
        JOIN group_permission gp ON gp.group_id = g.id
        JOIN mst_permission p ON p.id = gp.permission_id
    ),
    all_role_group_perms AS (
        SELECT
            rp_module_id AS ar_module_id,
            rp_function_id AS ar_function_id,
            rp_permission_level AS ar_permission_level,
            rp_enabled_flag AS ar_enabled_flag
        FROM role_perms
        UNION ALL
        SELECT
            gp_module_id,
            gp_function_id,
            gp_permission_level,
            gp_enabled_flag
        FROM group_perms
    ),
    resolved_role_group_perms AS (
        SELECT DISTINCT ON (ar_module_id, ar_function_id)
            ar_module_id,
            ar_function_id,
            ar_permission_level,
            ar_enabled_flag
        FROM all_role_group_perms
        ORDER BY ar_module_id, ar_function_id, ar_permission_level DESC
    ),
    all_permissions AS (
        SELECT
            up_module_id AS ap_module_id,
            up_function_id AS ap_function_id,
            up_permission_level AS ap_permission_level,
            up_enabled_flag AS ap_enabled_flag,
            p.permission_name AS ap_permission_name
        FROM user_perms up
        JOIN mst_permission p ON p.module_id = up.up_module_id AND p.function_id = up.up_function_id
        UNION ALL
        SELECT
            ar_module_id,
            ar_function_id,
            ar_permission_level,
            ar_enabled_flag,
            p.permission_name AS ap_permission_name
        FROM resolved_role_group_perms r
        JOIN mst_permission p ON p.module_id = r.ar_module_id AND p.function_id = r.ar_function_id
    ),
    effective_permissions AS (
        SELECT DISTINCT ON (ap_module_id, ap_function_id)
            ap_module_id,
            ap_function_id,
            ap_permission_level,
            ap_enabled_flag,
            ap_permission_name
        FROM all_permissions
        ORDER BY ap_module_id, ap_function_id,
            CASE
                WHEN EXISTS (
                    SELECT 1 FROM user_perms up
                    WHERE up.up_module_id = all_permissions.ap_module_id
                      AND up.up_function_id = all_permissions.ap_function_id
                ) THEN 1
                ELSE 2
            END,
            ap_permission_level DESC
    )
    SELECT
        ub.id AS user_id,
        ub.username,
        ub.user_status,
        ub.verified_at,
        ep.ap_module_id AS module_id,
        ep.ap_function_id AS function_id,
        ep.ap_permission_name AS permission_name,
        ep.ap_permission_level AS permission_level,
        ep.ap_enabled_flag AS enabled_flag
    FROM user_base ub
    CROSS JOIN effective_permissions ep;
END;
$$;

ALTER FUNCTION get_user_auth(text) OWNER TO postgres;
GRANT EXECUTE ON FUNCTION get_user_auth(text) TO baseappadmin;
