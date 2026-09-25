-- ============================================================
-- V9
-- Create the core practitioner table.
--
-- Security / tenancy model:
-- - Every practitioner belongs to exactly one organization.
-- - Every practitioner is linked to exactly one IAM organization
--   membership.
-- - The membership must belong to the same organization.
-- - Practitioner code is unique inside an organization.
-- - One organization membership has at most one practitioner
--   profile in that organization.
-- - Creation/update actors are recorded for accountability.
--
-- IAM roles remain authorization concerns.
-- practitioner_type represents the professional profile type.
-- ============================================================


-- ============================================================
-- 1. SUPPORT COMPOSITE ORGANIZATION/MEMBERSHIP FOREIGN KEY
--
-- Allows practitioner.practitioners to enforce:
--
-- practitioner.organization_id
--        +
-- practitioner.membership_id
--
-- must refer to a membership belonging to that organization.
-- ============================================================

ALTER TABLE iam.organization_memberships
    ADD CONSTRAINT uq_memberships_organization_id_id
    UNIQUE (
        organization_id,
        id
    );


-- ============================================================
-- 2. PRACTITIONERS
-- ============================================================

CREATE TABLE practitioner.practitioners (

    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,

    membership_id UUID NOT NULL,

    practitioner_code VARCHAR(50) NOT NULL,

    full_name VARCHAR(255) NOT NULL,

    practitioner_type VARCHAR(30) NOT NULL,

    license_number VARCHAR(100),

    specialty VARCHAR(150),

    phone VARCHAR(30),

    email VARCHAR(255),

    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    created_by_user_id UUID NOT NULL,

    updated_by_user_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,


    -- --------------------------------------------------------
    -- ORGANIZATION
    -- --------------------------------------------------------

    CONSTRAINT fk_practitioner_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id),


    -- --------------------------------------------------------
    -- IAM ORGANIZATION MEMBERSHIP
    --
    -- Composite FK prevents this invalid state:
    --
    -- practitioner.organization_id = Organization A
    -- membership_id               = membership in Organization B
    -- --------------------------------------------------------

    CONSTRAINT fk_practitioner_membership
        FOREIGN KEY (
            organization_id,
            membership_id
        )
        REFERENCES iam.organization_memberships (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- AUDIT ACTORS
    -- --------------------------------------------------------

    CONSTRAINT fk_practitioner_created_by_user
        FOREIGN KEY (created_by_user_id)
        REFERENCES iam.users(id),

    CONSTRAINT fk_practitioner_updated_by_user
        FOREIGN KEY (updated_by_user_id)
        REFERENCES iam.users(id),


    -- --------------------------------------------------------
    -- BUSINESS CONSTRAINTS
    -- --------------------------------------------------------

    CONSTRAINT uq_practitioner_code_per_organization
        UNIQUE (
            organization_id,
            practitioner_code
        ),

    CONSTRAINT uq_practitioner_membership_per_organization
        UNIQUE (
            organization_id,
            membership_id
        ),

    CONSTRAINT ck_practitioner_code_not_blank
        CHECK (
            char_length(trim(practitioner_code)) > 0
        ),

    CONSTRAINT ck_practitioner_full_name_not_blank
        CHECK (
            char_length(trim(full_name)) > 0
        ),

    CONSTRAINT ck_practitioner_type
        CHECK (
            practitioner_type IN (
                'DOCTOR',
                'NURSE',
                'PHARMACIST',
                'LAB_TECHNICIAN',
                'OTHER'
            )
        ),

    CONSTRAINT ck_practitioner_status
        CHECK (
            status IN (
                'ACTIVE',
                'INACTIVE',
                'SUSPENDED'
            )
        ),

    CONSTRAINT ck_practitioner_license_number_not_blank
        CHECK (
            license_number IS NULL
            OR char_length(trim(license_number)) > 0
        ),

    CONSTRAINT ck_practitioner_specialty_not_blank
        CHECK (
            specialty IS NULL
            OR char_length(trim(specialty)) > 0
        )
);


-- ============================================================
-- 3. INDEXES
-- ============================================================

CREATE INDEX idx_practitioners_organization_id
    ON practitioner.practitioners (
        organization_id
    );

CREATE INDEX idx_practitioners_membership_id
    ON practitioner.practitioners (
        membership_id
    );

CREATE INDEX idx_practitioners_org_status
    ON practitioner.practitioners (
        organization_id,
        status
    );

CREATE INDEX idx_practitioners_type
    ON practitioner.practitioners (
        practitioner_type
    );

CREATE INDEX idx_practitioners_license_number
    ON practitioner.practitioners (
        license_number
    );