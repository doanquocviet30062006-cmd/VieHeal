-- ============================================================
-- V21
-- Create Reception Queue entries.
--
-- A QueueEntry represents the operational presence of a patient
-- at a facility for one scheduled appointment.
--
-- Lifecycle:
--
-- WAITING
--   -> CALLED
--   -> SERVING
--   -> COMPLETED
--
-- WAITING / CALLED / SERVING
--   -> CANCELLED
--
-- Queue completion is operational only.
-- Clinical care remains owned by Encounter.
-- ============================================================


-- ============================================================
-- 1. SUPPORT STRONG APPOINTMENT REFERENCE
--
-- Queue entries intentionally denormalize patient_id and
-- practitioner_id for operational queries.
--
-- This composite unique constraint allows the queue table to
-- guarantee at DATABASE level that:
--
-- organization
-- facility
-- appointment
-- patient
-- practitioner
--
-- all describe the same Appointment row.
-- ============================================================

ALTER TABLE appointment.appointments
    ADD CONSTRAINT uq_appointments_queue_reference
    UNIQUE (
        organization_id,
        facility_id,
        id,
        patient_id,
        practitioner_id
    );


-- ============================================================
-- 2. QUEUE ENTRIES
-- ============================================================

CREATE TABLE reception.queue_entries (

    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,

    facility_id UUID NOT NULL,

    appointment_id UUID NOT NULL,

    patient_id UUID NOT NULL,

    practitioner_id UUID NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',

    checked_in_at TIMESTAMPTZ NOT NULL,

    called_at TIMESTAMPTZ,

    serving_started_at TIMESTAMPTZ,

    completed_at TIMESTAMPTZ,

    cancelled_at TIMESTAMPTZ,

    note VARCHAR(1000),

    created_by_user_id UUID NOT NULL,

    updated_by_user_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,


    -- --------------------------------------------------------
    -- ORGANIZATION
    -- --------------------------------------------------------

    CONSTRAINT fk_queue_entry_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id),


    -- --------------------------------------------------------
    -- FACILITY
    -- --------------------------------------------------------

    CONSTRAINT fk_queue_entry_facility
        FOREIGN KEY (
            organization_id,
            facility_id
        )
        REFERENCES organization.facilities (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- APPOINTMENT
    --
    -- Also guarantees patient_id and practitioner_id are copied
    -- from the referenced appointment rather than supplied from
    -- an unrelated tenant/resource.
    -- --------------------------------------------------------

    CONSTRAINT fk_queue_entry_appointment
        FOREIGN KEY (
            organization_id,
            facility_id,
            appointment_id,
            patient_id,
            practitioner_id
        )
        REFERENCES appointment.appointments (
            organization_id,
            facility_id,
            id,
            patient_id,
            practitioner_id
        ),


    -- --------------------------------------------------------
    -- AUDIT ACTORS
    -- --------------------------------------------------------

    CONSTRAINT fk_queue_entry_created_by_user
        FOREIGN KEY (created_by_user_id)
        REFERENCES iam.users(id),

    CONSTRAINT fk_queue_entry_updated_by_user
        FOREIGN KEY (updated_by_user_id)
        REFERENCES iam.users(id),


    -- --------------------------------------------------------
    -- ONE CHECK-IN PER APPOINTMENT
    -- --------------------------------------------------------

    CONSTRAINT uq_queue_entry_per_appointment
        UNIQUE (
            organization_id,
            appointment_id
        ),


    -- --------------------------------------------------------
    -- STATUS
    -- --------------------------------------------------------

    CONSTRAINT ck_queue_entry_status
        CHECK (
            status IN (
                'WAITING',
                'CALLED',
                'SERVING',
                'COMPLETED',
                'CANCELLED'
            )
        ),


    -- --------------------------------------------------------
    -- NOTE
    -- --------------------------------------------------------

    CONSTRAINT ck_queue_entry_note_not_blank
        CHECK (
            note IS NULL
            OR char_length(trim(note)) > 0
        ),


    -- --------------------------------------------------------
    -- LIFECYCLE TIMESTAMP CONSISTENCY
    -- --------------------------------------------------------

    CONSTRAINT ck_queue_entry_lifecycle_timestamps
        CHECK (
            (
                status = 'WAITING'
                AND called_at IS NULL
                AND serving_started_at IS NULL
                AND completed_at IS NULL
                AND cancelled_at IS NULL
            )
            OR
            (
                status = 'CALLED'
                AND called_at IS NOT NULL
                AND serving_started_at IS NULL
                AND completed_at IS NULL
                AND cancelled_at IS NULL
            )
            OR
            (
                status = 'SERVING'
                AND called_at IS NOT NULL
                AND serving_started_at IS NOT NULL
                AND completed_at IS NULL
                AND cancelled_at IS NULL
            )
            OR
            (
                status = 'COMPLETED'
                AND called_at IS NOT NULL
                AND serving_started_at IS NOT NULL
                AND completed_at IS NOT NULL
                AND cancelled_at IS NULL
            )
            OR
            (
                status = 'CANCELLED'
                AND completed_at IS NULL
                AND cancelled_at IS NOT NULL
            )
        ),


    -- --------------------------------------------------------
    -- TIMESTAMP ORDERING
    -- --------------------------------------------------------

    CONSTRAINT ck_queue_entry_called_after_checkin
        CHECK (
            called_at IS NULL
            OR called_at >= checked_in_at
        ),

    CONSTRAINT ck_queue_entry_serving_after_called
        CHECK (
            serving_started_at IS NULL
            OR (
                called_at IS NOT NULL
                AND serving_started_at >= called_at
            )
        ),

    CONSTRAINT ck_queue_entry_completed_after_serving
        CHECK (
            completed_at IS NULL
            OR (
                serving_started_at IS NOT NULL
                AND completed_at >= serving_started_at
            )
        ),

    CONSTRAINT ck_queue_entry_cancelled_after_checkin
        CHECK (
            cancelled_at IS NULL
            OR cancelled_at >= checked_in_at
        ),


    -- --------------------------------------------------------
    -- SUPPORT FUTURE TENANT-SAFE REFERENCES
    -- --------------------------------------------------------

    CONSTRAINT uq_queue_entries_organization_id_id
        UNIQUE (
            organization_id,
            id
        )
);


-- ============================================================
-- 3. INDEXES
-- ============================================================

CREATE INDEX idx_queue_entries_organization_id
    ON reception.queue_entries (
        organization_id
    );

CREATE INDEX idx_queue_entries_facility_id
    ON reception.queue_entries (
        facility_id
    );

CREATE INDEX idx_queue_entries_patient_id
    ON reception.queue_entries (
        patient_id
    );

CREATE INDEX idx_queue_entries_practitioner_id
    ON reception.queue_entries (
        practitioner_id
    );

CREATE INDEX idx_queue_entries_org_facility_status_order
    ON reception.queue_entries (
        organization_id,
        facility_id,
        status,
        checked_in_at,
        id
    );

CREATE INDEX idx_queue_entries_org_practitioner_status
    ON reception.queue_entries (
        organization_id,
        practitioner_id,
        status,
        checked_in_at
    );

CREATE INDEX idx_queue_entries_org_patient
    ON reception.queue_entries (
        organization_id,
        patient_id,
        checked_in_at
    );