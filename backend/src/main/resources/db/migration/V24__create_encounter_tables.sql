-- ============================================================
-- V24
-- Create core Encounter / EMR tables.
--
-- Ownership:
--
-- Appointment:
--   booking and scheduling
--
-- Reception Queue:
--   operational patient presence / queue lifecycle
--
-- Encounter:
--   clinical care session
--
-- Clinical Note:
--   core clinical narrative for the Encounter
--
-- Diagnosis, Observation, Prescription, Lab/Imaging and
-- other structured clinical data remain separate downstream
-- modules.
--
-- Encounter lifecycle:
--
-- IN_PROGRESS
--   -> COMPLETED
--
-- IN_PROGRESS
--   -> CANCELLED
--
-- Application layer will additionally enforce workflow rules:
--
-- - QueueEntry must be SERVING before Encounter starts.
-- - patient/practitioner are derived from Queue/Appointment.
-- - COMPLETED and CANCELLED are terminal states.
-- - completed Encounter clinical documentation is immutable.
-- ============================================================


-- ============================================================
-- 1. SUPPORT STRONG QUEUE -> ENCOUNTER REFERENCES
--
-- QueueEntry already stores:
--
-- organization_id
-- facility_id
-- appointment_id
-- patient_id
-- practitioner_id
--
-- This composite unique constraint allows Encounter to use a
-- strong foreign key proving that every copied identifier
-- belongs to the same QueueEntry.
-- ============================================================

ALTER TABLE reception.queue_entries
    ADD CONSTRAINT uq_queue_entries_encounter_reference
    UNIQUE (
        organization_id,
        facility_id,
        id,
        appointment_id,
        patient_id,
        practitioner_id
    );


-- ============================================================
-- 2. ENCOUNTERS
-- ============================================================

CREATE TABLE encounter.encounters (

    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,

    facility_id UUID NOT NULL,

    appointment_id UUID NOT NULL,

    queue_entry_id UUID NOT NULL,

    patient_id UUID NOT NULL,

    practitioner_id UUID NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS',

    started_at TIMESTAMPTZ NOT NULL,

    completed_at TIMESTAMPTZ,

    cancelled_at TIMESTAMPTZ,

    cancellation_reason VARCHAR(500),

    created_by_user_id UUID NOT NULL,

    updated_by_user_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,


    -- --------------------------------------------------------
    -- ORGANIZATION
    -- --------------------------------------------------------

    CONSTRAINT fk_encounter_organization
        FOREIGN KEY (
            organization_id
        )
        REFERENCES organization.organizations (
            id
        ),


    -- --------------------------------------------------------
    -- FACILITY
    --
    -- Guarantees that facility belongs to Encounter
    -- organization.
    -- --------------------------------------------------------

    CONSTRAINT fk_encounter_facility
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
    -- V21 established:
    --
    -- UNIQUE (
    --   organization_id,
    --   facility_id,
    --   id,
    --   patient_id,
    --   practitioner_id
    -- )
    --
    -- This prevents Encounter from copying an unrelated
    -- patient or practitioner.
    -- --------------------------------------------------------

    CONSTRAINT fk_encounter_appointment
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
    -- QUEUE ENTRY
    --
    -- Strong database-level link between Encounter and the
    -- operational queue visit that started it.
    -- --------------------------------------------------------

    CONSTRAINT fk_encounter_queue_entry
        FOREIGN KEY (
            organization_id,
            facility_id,
            queue_entry_id,
            appointment_id,
            patient_id,
            practitioner_id
        )
        REFERENCES reception.queue_entries (
            organization_id,
            facility_id,
            id,
            appointment_id,
            patient_id,
            practitioner_id
        ),


    -- --------------------------------------------------------
    -- AUDIT ACTORS
    -- --------------------------------------------------------

    CONSTRAINT fk_encounter_created_by_user
        FOREIGN KEY (
            created_by_user_id
        )
        REFERENCES iam.users (
            id
        ),

    CONSTRAINT fk_encounter_updated_by_user
        FOREIGN KEY (
            updated_by_user_id
        )
        REFERENCES iam.users (
            id
        ),


    -- --------------------------------------------------------
    -- ONE ENCOUNTER PER APPOINTMENT
    -- --------------------------------------------------------

    CONSTRAINT uq_encounter_per_appointment
        UNIQUE (
            organization_id,
            appointment_id
        ),


    -- --------------------------------------------------------
    -- ONE ENCOUNTER PER QUEUE ENTRY
    -- --------------------------------------------------------

    CONSTRAINT uq_encounter_per_queue_entry
        UNIQUE (
            organization_id,
            queue_entry_id
        ),


    -- --------------------------------------------------------
    -- STATUS
    -- --------------------------------------------------------

    CONSTRAINT ck_encounter_status
        CHECK (
            status IN (
                'IN_PROGRESS',
                'COMPLETED',
                'CANCELLED'
            )
        ),


    -- --------------------------------------------------------
    -- LIFECYCLE TIMESTAMP CONSISTENCY
    -- --------------------------------------------------------

    CONSTRAINT ck_encounter_lifecycle_timestamps
        CHECK (
            (
                status = 'IN_PROGRESS'
                AND completed_at IS NULL
                AND cancelled_at IS NULL
                AND cancellation_reason IS NULL
            )
            OR
            (
                status = 'COMPLETED'
                AND completed_at IS NOT NULL
                AND cancelled_at IS NULL
                AND cancellation_reason IS NULL
            )
            OR
            (
                status = 'CANCELLED'
                AND completed_at IS NULL
                AND cancelled_at IS NOT NULL
                AND cancellation_reason IS NOT NULL
            )
        ),


    -- --------------------------------------------------------
    -- CANCELLATION REASON
    -- --------------------------------------------------------

    CONSTRAINT ck_encounter_cancellation_reason_not_blank
        CHECK (
            cancellation_reason IS NULL
            OR char_length(
                trim(cancellation_reason)
            ) > 0
        ),


    -- --------------------------------------------------------
    -- TIMESTAMP ORDERING
    -- --------------------------------------------------------

    CONSTRAINT ck_encounter_completed_after_start
        CHECK (
            completed_at IS NULL
            OR completed_at >= started_at
        ),

    CONSTRAINT ck_encounter_cancelled_after_start
        CHECK (
            cancelled_at IS NULL
            OR cancelled_at >= started_at
        ),


    -- --------------------------------------------------------
    -- SUPPORT FUTURE TENANT-SAFE REFERENCES
    --
    -- Diagnosis / Observation / Prescription / Lab / Imaging
    -- can safely reference Encounter in later migrations.
    -- --------------------------------------------------------

    CONSTRAINT uq_encounters_organization_id_id
        UNIQUE (
            organization_id,
            id
        ),

    CONSTRAINT uq_encounters_org_facility_id
        UNIQUE (
            organization_id,
            facility_id,
            id
        )
);


-- ============================================================
-- 3. CLINICAL NOTES
--
-- One core clinical note document per Encounter.
--
-- This table intentionally contains narrative documentation
-- only.
--
-- Structured clinical facts belong to later modules:
--
-- diagnosis
-- observation
-- prescription
-- lab
-- imaging
-- ============================================================

CREATE TABLE encounter.clinical_notes (

    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,

    facility_id UUID NOT NULL,

    encounter_id UUID NOT NULL,

    chief_complaint VARCHAR(2000),

    subjective TEXT,

    objective TEXT,

    assessment TEXT,

    plan TEXT,

    created_by_user_id UUID NOT NULL,

    updated_by_user_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,


    -- --------------------------------------------------------
    -- ENCOUNTER
    --
    -- Prevents a clinical note from being attached to an
    -- Encounter belonging to another organization/facility.
    -- --------------------------------------------------------

    CONSTRAINT fk_clinical_note_encounter
        FOREIGN KEY (
            organization_id,
            facility_id,
            encounter_id
        )
        REFERENCES encounter.encounters (
            organization_id,
            facility_id,
            id
        ),


    -- --------------------------------------------------------
    -- AUDIT ACTORS
    -- --------------------------------------------------------

    CONSTRAINT fk_clinical_note_created_by_user
        FOREIGN KEY (
            created_by_user_id
        )
        REFERENCES iam.users (
            id
        ),

    CONSTRAINT fk_clinical_note_updated_by_user
        FOREIGN KEY (
            updated_by_user_id
        )
        REFERENCES iam.users (
            id
        ),


    -- --------------------------------------------------------
    -- ONE CORE CLINICAL NOTE PER ENCOUNTER
    -- --------------------------------------------------------

    CONSTRAINT uq_clinical_note_per_encounter
        UNIQUE (
            organization_id,
            encounter_id
        ),


    -- --------------------------------------------------------
    -- NON-BLANK CONTENT
    --
    -- NULL means the section is not documented yet.
    -- Whitespace-only documentation is rejected.
    -- --------------------------------------------------------

    CONSTRAINT ck_clinical_note_chief_complaint_not_blank
        CHECK (
            chief_complaint IS NULL
            OR char_length(
                trim(chief_complaint)
            ) > 0
        ),

    CONSTRAINT ck_clinical_note_subjective_not_blank
        CHECK (
            subjective IS NULL
            OR char_length(
                trim(subjective)
            ) > 0
        ),

    CONSTRAINT ck_clinical_note_objective_not_blank
        CHECK (
            objective IS NULL
            OR char_length(
                trim(objective)
            ) > 0
        ),

    CONSTRAINT ck_clinical_note_assessment_not_blank
        CHECK (
            assessment IS NULL
            OR char_length(
                trim(assessment)
            ) > 0
        ),

    CONSTRAINT ck_clinical_note_plan_not_blank
        CHECK (
            plan IS NULL
            OR char_length(
                trim(plan)
            ) > 0
        ),


    -- --------------------------------------------------------
    -- DEFENSIVE MAXIMUM LENGTHS
    -- --------------------------------------------------------

    CONSTRAINT ck_clinical_note_subjective_length
        CHECK (
            subjective IS NULL
            OR char_length(subjective) <= 20000
        ),

    CONSTRAINT ck_clinical_note_objective_length
        CHECK (
            objective IS NULL
            OR char_length(objective) <= 20000
        ),

    CONSTRAINT ck_clinical_note_assessment_length
        CHECK (
            assessment IS NULL
            OR char_length(assessment) <= 20000
        ),

    CONSTRAINT ck_clinical_note_plan_length
        CHECK (
            plan IS NULL
            OR char_length(plan) <= 20000
        ),


    -- --------------------------------------------------------
    -- SUPPORT FUTURE TENANT-SAFE REFERENCES
    -- --------------------------------------------------------

    CONSTRAINT uq_clinical_notes_organization_id_id
        UNIQUE (
            organization_id,
            id
        )
);


-- ============================================================
-- 4. ENCOUNTER INDEXES
-- ============================================================

CREATE INDEX idx_encounters_organization_id
    ON encounter.encounters (
        organization_id
    );

CREATE INDEX idx_encounters_org_facility_status_started
    ON encounter.encounters (
        organization_id,
        facility_id,
        status,
        started_at
    );

CREATE INDEX idx_encounters_org_patient_started
    ON encounter.encounters (
        organization_id,
        patient_id,
        started_at
    );

CREATE INDEX idx_encounters_org_practitioner_started
    ON encounter.encounters (
        organization_id,
        practitioner_id,
        started_at
    );

CREATE INDEX idx_encounters_appointment_id
    ON encounter.encounters (
        appointment_id
    );

CREATE INDEX idx_encounters_queue_entry_id
    ON encounter.encounters (
        queue_entry_id
    );


-- ============================================================
-- 5. CLINICAL NOTE INDEXES
-- ============================================================

CREATE INDEX idx_clinical_notes_organization_id
    ON encounter.clinical_notes (
        organization_id
    );

CREATE INDEX idx_clinical_notes_encounter_id
    ON encounter.clinical_notes (
        encounter_id
    );

CREATE INDEX idx_clinical_notes_org_facility
    ON encounter.clinical_notes (
        organization_id,
        facility_id
    );