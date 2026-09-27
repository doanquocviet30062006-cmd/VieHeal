-- V25
-- Adds platform-level user-to-role assignments for SYSTEM-scoped roles.
--
-- Organization-scoped roles continue to use:
--   iam.organization_memberships
--   iam.membership_roles
--
-- SYSTEM roles are assigned directly to IAM users.

ALTER TABLE iam.roles
    ADD CONSTRAINT uq_iam_roles_id_scope
        UNIQUE (id, scope);


CREATE TABLE iam.user_system_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,

    role_scope VARCHAR(30) NOT NULL DEFAULT 'SYSTEM',

    assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_system_role_user
        FOREIGN KEY (user_id)
        REFERENCES iam.users(id),

    CONSTRAINT fk_user_system_role_role_scope
        FOREIGN KEY (role_id, role_scope)
        REFERENCES iam.roles(id, scope),

    CONSTRAINT ck_user_system_role_scope
        CHECK (role_scope = 'SYSTEM')
);


CREATE INDEX idx_user_system_roles_role
    ON iam.user_system_roles(role_id);


INSERT INTO iam.role_permissions (
    role_id,
    permission_id
)
SELECT
    role.id,
    permission.id
FROM iam.roles role
CROSS JOIN iam.permissions permission
WHERE role.code = 'SYSTEM_ADMIN'
  AND role.scope = 'SYSTEM'
  AND permission.code = 'organization.manage'
ON CONFLICT DO NOTHING;