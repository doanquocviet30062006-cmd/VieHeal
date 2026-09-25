-- ============================================================
-- V7
-- Create the core patient table.
--
-- Security model:
-- - Every patient belongs to exactly one organization.
-- - Every patient has one managing facility for the MVP.
-- - The facility must belong to the same organization.
-- - Patient code is unique inside an organization.
-- - Creation/update actors are recorded for accountability.
--
-- Future clinical data such as encounters, diagnoses,
-- observations, orders, medications, etc. will live in
-- their own modules/tables.
-- ============================================================


-- ============================================================
-- 1. SUPPORT COMPOSITE TENANT/FACILITY FOREIGN KEYS
--
-- This allows patient.patients to enforce at DATABASE level:
--
-- patient.organization_id
--        +
-- patient.managing_facility_id
--
-- must refer to a facility belonging to that same organization.
-- ============================================================

ALTER TABLE organization.facilities
    ADD CONSTRAINT uq_facilities_organization_id_id
    UNIQUE (organization_id, id);


-- ============================================================
-- 2. PATIENTS
-- ============================================================

CREATE TABLE patient.patients (

    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,

    managing_facility_id UUID NOT NULL,

    patient_code VARCHAR(50) NOT NULL,

    full_name VARCHAR(255) NOT NULL,

    date_of_birth DATE,

    sex VARCHAR(20) NOT NULL DEFAULT 'UNKNOWN',

    phone VARCHAR(30),

    email VARCHAR(255),

    address_line VARCHAR(255),

    ward VARCHAR(100),

    district VARCHAR(100),

    province VARCHAR(100),

    country_code VARCHAR(2) NOT NULL DEFAULT 'VN',

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_by_user_id UUID NOT NULL,

    updated_by_user_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,


    -- --------------------------------------------------------
    -- ORGANIZATION
    -- --------------------------------------------------------

    CONSTRAINT fk_patient_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id),


    -- --------------------------------------------------------
    -- MANAGING FACILITY
    --
    -- Composite FK prevents this invalid state:
    --
    -- organization = Clinic A
    -- facility     = a facility belonging to Clinic B
    -- --------------------------------------------------------

    CONSTRAINT fk_patient_managing_facility
        FOREIGN KEY (
            organization_id,
            managing_facility_id
        )
        REFERENCES organization.facilities (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- AUDIT ACTORS
    -- --------------------------------------------------------

    CONSTRAINT fk_patient_created_by_user
        FOREIGN KEY (created_by_user_id)
        REFERENCES iam.users(id),

    CONSTRAINT fk_patient_updated_by_user
        FOREIGN KEY (updated_by_user_id)
        REFERENCES iam.users(id),


    -- --------------------------------------------------------
    -- BUSINESS CONSTRAINTS
    -- --------------------------------------------------------

    CONSTRAINT uq_patient_code_per_organization
        UNIQUE (
            organization_id,
            patient_code
        ),

    CONSTRAINT ck_patient_code_not_blank
        CHECK (
            char_length(trim(patient_code)) > 0
        ),

    CONSTRAINT ck_patient_full_name_not_blank
        CHECK (
            char_length(trim(full_name)) > 0
        ),

    CONSTRAINT ck_patient_sex
        CHECK (
            sex IN (
                'MALE',
                'FEMALE',
                'OTHER',
                'UNKNOWN'
            )
        ),

    CONSTRAINT ck_patient_status
        CHECK (
            status IN (
                'ACTIVE',
                'INACTIVE',
                'DECEASED'
            )
        ),

    CONSTRAINT ck_patient_country_code
        CHECK (
            country_code ~ '^[A-Z]{2}$'
        )
);


-- ============================================================
-- 3. INDEXES
-- ============================================================

CREATE INDEX idx_patients_organization_id
    ON patient.patients (
        organization_id
    );

CREATE INDEX idx_patients_managing_facility_id
    ON patient.patients (
        managing_facility_id
    );

CREATE INDEX idx_patients_org_facility_status
    ON patient.patients (
        organization_id,
        managing_facility_id,
        status
    );

CREATE INDEX idx_patients_phone
    ON patient.patients (
        phone
    );