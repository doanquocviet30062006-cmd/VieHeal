-- ============================================================
-- V18
-- Create the core Appointment table.
--
-- Appointment represents a concrete patient booking.
--
-- Tenant safety:
-- - appointment belongs to exactly one organization.
-- - facility must belong to that organization.
-- - patient must belong to that organization.
-- - practitioner must belong to that organization.
-- - facility_service must belong to both the same organization
--   and the same facility.
--
-- Scheduling availability is validated by the application layer.
--
-- Database exclusion constraints provide a final concurrency-safe
-- guard against double-booking an active appointment.
-- ============================================================


-- ============================================================
-- 1. SUPPORT TENANT-SAFE PATIENT FOREIGN KEYS
-- ============================================================

ALTER TABLE patient.patients
    ADD CONSTRAINT uq_patients_organization_id_id
    UNIQUE (
        organization_id,
        id
    );


-- ============================================================
-- 2. SUPPORT TENANT/FACILITY-SAFE FACILITY SERVICE FOREIGN KEYS
--
-- Allows Appointment to guarantee that facility_service_id
-- belongs to both the organization and facility in the
-- appointment row.
-- ============================================================

ALTER TABLE service_catalog.facility_services
    ADD CONSTRAINT uq_facility_services_organization_facility_id
    UNIQUE (
        organization_id,
        facility_id,
        id
    );


-- ============================================================
-- 3. POSTGRESQL SUPPORT FOR EXCLUSION CONSTRAINTS
--
-- UUID equality in GiST requires btree_gist.
-- ============================================================

CREATE EXTENSION IF NOT EXISTS btree_gist;


-- ============================================================
-- 4. APPOINTMENTS
-- ============================================================

CREATE TABLE appointment.appointments (

    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,

    facility_id UUID NOT NULL,

    patient_id UUID NOT NULL,

    practitioner_id UUID NOT NULL,

    facility_service_id UUID NOT NULL,

    scheduled_start_at TIMESTAMPTZ NOT NULL,

    scheduled_end_at TIMESTAMPTZ NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',

    reason VARCHAR(1000),

    cancellation_reason VARCHAR(500),

    created_by_user_id UUID NOT NULL,

    updated_by_user_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,


    -- --------------------------------------------------------
    -- ORGANIZATION
    -- --------------------------------------------------------

    CONSTRAINT fk_appointment_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id),


    -- --------------------------------------------------------
    -- FACILITY
    -- --------------------------------------------------------

    CONSTRAINT fk_appointment_facility
        FOREIGN KEY (
            organization_id,
            facility_id
        )
        REFERENCES organization.facilities (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- PATIENT
    --
    -- Patient only needs to belong to the same organization.
    -- Its managing facility does NOT need to equal the
    -- appointment facility.
    -- --------------------------------------------------------

    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (
            organization_id,
            patient_id
        )
        REFERENCES patient.patients (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- PRACTITIONER
    --
    -- V15 established:
    -- UNIQUE (organization_id, id)
    -- on practitioner.practitioners.
    --
    -- Facility assignment is validated by the application
    -- because assignment has its own ACTIVE/INACTIVE lifecycle.
    -- --------------------------------------------------------

    CONSTRAINT fk_appointment_practitioner
        FOREIGN KEY (
            organization_id,
            practitioner_id
        )
        REFERENCES practitioner.practitioners (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- FACILITY SERVICE
    --
    -- Composite FK guarantees the selected facility service
    -- belongs to the appointment organization AND facility.
    -- --------------------------------------------------------

    CONSTRAINT fk_appointment_facility_service
        FOREIGN KEY (
            organization_id,
            facility_id,
            facility_service_id
        )
        REFERENCES service_catalog.facility_services (
            organization_id,
            facility_id,
            id
        ),


    -- --------------------------------------------------------
    -- AUDIT ACTORS
    -- --------------------------------------------------------

    CONSTRAINT fk_appointment_created_by_user
        FOREIGN KEY (created_by_user_id)
        REFERENCES iam.users(id),

    CONSTRAINT fk_appointment_updated_by_user
        FOREIGN KEY (updated_by_user_id)
        REFERENCES iam.users(id),


    -- --------------------------------------------------------
    -- BUSINESS CONSTRAINTS
    -- --------------------------------------------------------

    CONSTRAINT ck_appointment_time_range
        CHECK (
            scheduled_start_at < scheduled_end_at
        ),

    CONSTRAINT ck_appointment_status
        CHECK (
            status IN (
                'SCHEDULED',
                'CANCELLED',
                'COMPLETED',
                'NO_SHOW'
            )
        ),

    CONSTRAINT ck_appointment_reason_not_blank
        CHECK (
            reason IS NULL
            OR char_length(trim(reason)) > 0
        ),

    CONSTRAINT ck_appointment_cancellation_reason_not_blank
        CHECK (
            cancellation_reason IS NULL
            OR char_length(trim(cancellation_reason)) > 0
        ),

    CONSTRAINT ck_appointment_cancellation_reason_state
        CHECK (
            (
                status = 'CANCELLED'
                AND cancellation_reason IS NOT NULL
            )
            OR
            (
                status <> 'CANCELLED'
                AND cancellation_reason IS NULL
            )
        ),


    -- --------------------------------------------------------
    -- PRACTITIONER DOUBLE-BOOKING PROTECTION
    --
    -- [start, end) semantics:
    --
    -- 08:00-10:00
    -- 10:00-11:00
    --
    -- are allowed because they only touch at the boundary.
    --
    -- Only SCHEDULED appointments reserve future calendar time.
    -- --------------------------------------------------------

    CONSTRAINT ex_appointment_practitioner_schedule_overlap
        EXCLUDE USING gist (
            organization_id WITH =,
            practitioner_id WITH =,
            tstzrange(
                scheduled_start_at,
                scheduled_end_at,
                '[)'
            ) WITH &&
        )
        WHERE (
            status = 'SCHEDULED'
        ),


    -- --------------------------------------------------------
    -- PATIENT DOUBLE-BOOKING PROTECTION
    --
    -- Prevent one patient from holding overlapping active
    -- appointments, including at different facilities.
    -- --------------------------------------------------------

    CONSTRAINT ex_appointment_patient_schedule_overlap
        EXCLUDE USING gist (
            organization_id WITH =,
            patient_id WITH =,
            tstzrange(
                scheduled_start_at,
                scheduled_end_at,
                '[)'
            ) WITH &&
        )
        WHERE (
            status = 'SCHEDULED'
        ),


    -- --------------------------------------------------------
    -- SUPPORT TENANT-SAFE DOWNSTREAM REFERENCES
    --
    -- Queue / Encounter and future modules can reference an
    -- appointment together with organization_id.
    -- --------------------------------------------------------

    CONSTRAINT uq_appointments_organization_id_id
        UNIQUE (
            organization_id,
            id
        )
);


-- ============================================================
-- 5. INDEXES
-- ============================================================

CREATE INDEX idx_appointments_organization_id
    ON appointment.appointments (
        organization_id
    );

CREATE INDEX idx_appointments_org_facility_start
    ON appointment.appointments (
        organization_id,
        facility_id,
        scheduled_start_at
    );

CREATE INDEX idx_appointments_org_patient_start
    ON appointment.appointments (
        organization_id,
        patient_id,
        scheduled_start_at
    );

CREATE INDEX idx_appointments_org_practitioner_start
    ON appointment.appointments (
        organization_id,
        practitioner_id,
        scheduled_start_at
    );

CREATE INDEX idx_appointments_facility_service_id
    ON appointment.appointments (
        facility_service_id
    );

CREATE INDEX idx_appointments_org_facility_status_start
    ON appointment.appointments (
        organization_id,
        facility_id,
        status,
        scheduled_start_at
    );