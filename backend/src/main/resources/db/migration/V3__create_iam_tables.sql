CREATE TABLE iam.users (
    id UUID PRIMARY KEY,

    external_subject VARCHAR(255),
    identity_provider VARCHAR(50),

    email VARCHAR(255),
    phone VARCHAR(30),
    display_name VARCHAR(255) NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_iam_user_external_identity
        UNIQUE (identity_provider, external_subject),

    CONSTRAINT uq_iam_user_email
        UNIQUE (email),

    CONSTRAINT ck_iam_user_status
        CHECK (status IN (
            'ACTIVE',
            'INACTIVE',
            'SUSPENDED'
        ))
);


CREATE TABLE iam.roles (
    id UUID PRIMARY KEY,

    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),

    scope VARCHAR(30) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_iam_role_scope
        CHECK (scope IN (
            'SYSTEM',
            'ORGANIZATION'
        ))
);


CREATE TABLE iam.permissions (
    id UUID PRIMARY KEY,

    code VARCHAR(150) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE iam.role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,

    PRIMARY KEY (role_id, permission_id),

    CONSTRAINT fk_role_permission_role
        FOREIGN KEY (role_id)
        REFERENCES iam.roles(id),

    CONSTRAINT fk_role_permission_permission
        FOREIGN KEY (permission_id)
        REFERENCES iam.permissions(id)
);


CREATE TABLE iam.organization_memberships (
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,
    organization_id UUID NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    joined_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_membership_user
        FOREIGN KEY (user_id)
        REFERENCES iam.users(id),

    CONSTRAINT fk_membership_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id),

    CONSTRAINT uq_user_organization_membership
        UNIQUE (user_id, organization_id),

    CONSTRAINT ck_membership_status
        CHECK (status IN (
            'ACTIVE',
            'INACTIVE',
            'SUSPENDED'
        ))
);


CREATE TABLE iam.membership_roles (
    membership_id UUID NOT NULL,
    role_id UUID NOT NULL,

    PRIMARY KEY (membership_id, role_id),

    CONSTRAINT fk_membership_role_membership
        FOREIGN KEY (membership_id)
        REFERENCES iam.organization_memberships(id),

    CONSTRAINT fk_membership_role_role
        FOREIGN KEY (role_id)
        REFERENCES iam.roles(id)
);


CREATE TABLE iam.facility_assignments (
    id UUID PRIMARY KEY,

    membership_id UUID NOT NULL,
    facility_id UUID NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMPTZ,

    CONSTRAINT fk_facility_assignment_membership
        FOREIGN KEY (membership_id)
        REFERENCES iam.organization_memberships(id),

    CONSTRAINT fk_facility_assignment_facility
        FOREIGN KEY (facility_id)
        REFERENCES organization.facilities(id),

    CONSTRAINT uq_membership_facility
        UNIQUE (membership_id, facility_id),

    CONSTRAINT ck_facility_assignment_status
        CHECK (status IN (
            'ACTIVE',
            'INACTIVE',
            'SUSPENDED'
        ))
);


CREATE INDEX idx_iam_users_external_subject
    ON iam.users(external_subject);

CREATE INDEX idx_membership_user
    ON iam.organization_memberships(user_id);

CREATE INDEX idx_membership_organization
    ON iam.organization_memberships(organization_id);

CREATE INDEX idx_facility_assignment_membership
    ON iam.facility_assignments(membership_id);

CREATE INDEX idx_facility_assignment_facility
    ON iam.facility_assignments(facility_id);