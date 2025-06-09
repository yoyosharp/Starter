-- Add foreign key constraints after all tables are created

-- User Role foreign keys
ALTER TABLE "user_role"
    ADD CONSTRAINT fk_user_role_user
    FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE;

ALTER TABLE "user_role"
    ADD CONSTRAINT fk_user_role_role
    FOREIGN KEY (role_id) REFERENCES "role" (id) ON DELETE CASCADE;

-- Role Permission foreign keys
ALTER TABLE "role_permission"
    ADD CONSTRAINT fk_role_permission_role
    FOREIGN KEY (role_id) REFERENCES "role" (id) ON DELETE CASCADE;

ALTER TABLE "role_permission"
    ADD CONSTRAINT fk_role_permission_permission
    FOREIGN KEY (permission_id) REFERENCES "permission" (id) ON DELETE CASCADE;

-- Group User foreign keys
ALTER TABLE "group_user"
    ADD CONSTRAINT fk_group_user_user
    FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE;

ALTER TABLE "group_user"
    ADD CONSTRAINT fk_group_user_group
    FOREIGN KEY (group_id) REFERENCES "group" (id) ON DELETE CASCADE;

-- Group Permission foreign keys
ALTER TABLE "group_permission"
    ADD CONSTRAINT fk_group_permission_group
    FOREIGN KEY (group_id) REFERENCES "group" (id) ON DELETE CASCADE;

ALTER TABLE "group_permission"
    ADD CONSTRAINT fk_group_permission_permission
    FOREIGN KEY (permission_id) REFERENCES "permission" (id) ON DELETE CASCADE;

-- User Permission foreign keys
ALTER TABLE "user_permission"
    ADD CONSTRAINT fk_user_permission_user
    FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE;

ALTER TABLE "user_permission"
    ADD CONSTRAINT fk_user_permission_permission
    FOREIGN KEY (permission_id) REFERENCES "permission" (id) ON DELETE CASCADE;
