-- ============================================================
-- V15
-- Create Scheduling module tables.
--
-- Design:
-- - Scheduling rules are facility-specific.
-- - Practitioners remain organization-scoped professional
--   profiles.
-- - A scheduling rule binds a practitioner to a facility.
-- - Recurring availability uses local facility time.
-- - Concrete exceptions use absolute timestamps.
-- - Time slots are calculated dynamically and are NOT stored.
-- - Appointment booking remains owned by the Appointment module.
-- ============================================================


-- ============================================================
-- 1. SUPPORT TENANT-SAFE PRACTITIONER FOREIGN KEYS
--
-- practitioner.id is already globally unique because it is the
-- primary key. This composite unique constraint additionally
-- allows downstream modules to enforce:
--
-- scheduling.organization_id
--        +
-- scheduling.practitioner_id
--
-- must refer to a practitioner in the same organization.
-- ============================================================

ALTER TABLE practitioner.practitioners
    ADD CONSTRAINT uq_practitioners_organization_id_id
    UNIQUE (
        organization_id,
        id
    );


-- ============================================================
-- 2. FACILITY SCHEDULING SETTINGS
--
-- One scheduling configuration per facility.
--
-- time_zone_id must contain an IANA time-zone identifier such
-- as:
--   Asia/Ho_Chi_Minh
--   Asia/Bangkok
--   America/New_York
--
-- Exact IANA validation belongs in the application/domain layer.
-- ============================================================

CREATE TABLE scheduling.facility_scheduling_settings (

    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,

    facility_id UUID NOT NULL,

    time_zone_id VARCHAR(100) NOT NULL,

    created_by_user_id UUID NOT NULL,

    updated_by_user_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,


    -- --------------------------------------------------------
    -- ORGANIZATION
    -- --------------------------------------------------------

    CONSTRAINT fk_facility_scheduling_settings_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id),


    -- --------------------------------------------------------
    -- FACILITY
    --
    -- Composite FK prevents:
    --
    -- organization_id = Organization A
    -- facility_id     = facility belonging to Organization B
    -- --------------------------------------------------------

    CONSTRAINT fk_facility_scheduling_settings_facility
        FOREIGN KEY (
            organization_id,
            facility_id
        )
        REFERENCES organization.facilities (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- AUDIT ACTORS
    -- --------------------------------------------------------

    CONSTRAINT fk_facility_scheduling_settings_created_by_user
        FOREIGN KEY (created_by_user_id)
        REFERENCES iam.users(id),

    CONSTRAINT fk_facility_scheduling_settings_updated_by_user
        FOREIGN KEY (updated_by_user_id)
        REFERENCES iam.users(id),


    -- --------------------------------------------------------
    -- BUSINESS CONSTRAINTS
    -- --------------------------------------------------------

    CONSTRAINT uq_facility_scheduling_settings_per_facility
        UNIQUE (
            organization_id,
            facility_id
        ),

    CONSTRAINT ck_facility_scheduling_settings_time_zone_not_blank
        CHECK (
            char_length(trim(time_zone_id)) > 0
        )
);


-- ============================================================
-- 3. PRACTITIONER AVAILABILITY RULES
--
-- Recurring weekly working periods.
--
-- ISO day-of-week:
--   1 = Monday
--   2 = Tuesday
--   ...
--   7 = Sunday
--
-- start_local_time / end_local_time are interpreted in the
-- facility's configured time zone.
--
-- A rule must not cross midnight. Cross-midnight availability
-- is represented by two rules on adjacent calendar days.
--
-- effective_to = NULL means the rule has no scheduled end date.
-- ============================================================

CREATE TABLE scheduling.practitioner_availability_rules (

    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,

    facility_id UUID NOT NULL,

    practitioner_id UUID NOT NULL,

    day_of_week SMALLINT NOT NULL,

    start_local_time TIME WITHOUT TIME ZONE NOT NULL,

    end_local_time TIME WITHOUT TIME ZONE NOT NULL,

    effective_from DATE NOT NULL,

    effective_to DATE,

    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    created_by_user_id UUID NOT NULL,

    updated_by_user_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,


    -- --------------------------------------------------------
    -- ORGANIZATION
    -- --------------------------------------------------------

    CONSTRAINT fk_availability_rule_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id),


    -- --------------------------------------------------------
    -- FACILITY
    -- --------------------------------------------------------

    CONSTRAINT fk_availability_rule_facility
        FOREIGN KEY (
            organization_id,
            facility_id
        )
        REFERENCES organization.facilities (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- PRACTITIONER
    -- --------------------------------------------------------

    CONSTRAINT fk_availability_rule_practitioner
        FOREIGN KEY (
            organization_id,
            practitioner_id
        )
        REFERENCES practitioner.practitioners (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- AUDIT ACTORS
    -- --------------------------------------------------------

    CONSTRAINT fk_availability_rule_created_by_user
        FOREIGN KEY (created_by_user_id)
        REFERENCES iam.users(id),

    CONSTRAINT fk_availability_rule_updated_by_user
        FOREIGN KEY (updated_by_user_id)
        REFERENCES iam.users(id),


    -- --------------------------------------------------------
    -- BUSINESS CONSTRAINTS
    -- --------------------------------------------------------

    CONSTRAINT ck_availability_rule_day_of_week
        CHECK (
            day_of_week BETWEEN 1 AND 7
        ),

    CONSTRAINT ck_availability_rule_time_range
        CHECK (
            start_local_time < end_local_time
        ),

    CONSTRAINT ck_availability_rule_effective_range
        CHECK (
            effective_to IS NULL
            OR effective_to >= effective_from
        ),

    CONSTRAINT ck_availability_rule_status
        CHECK (
            status IN (
                'ACTIVE',
                'INACTIVE'
            )
        )
);


-- ============================================================
-- 4. PRACTITIONER AVAILABILITY EXCEPTIONS
--
-- Concrete overrides to recurring availability.
--
-- AVAILABLE:
--   adds an exceptional working period.
--
-- UNAVAILABLE:
--   removes availability for a concrete period, e.g.
--   leave, training, emergency absence.
--
-- start_at / end_at are absolute timestamps because an
-- exception refers to a concrete real-world period.
-- ============================================================

CREATE TABLE scheduling.practitioner_availability_exceptions (

    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,

    facility_id UUID NOT NULL,

    practitioner_id UUID NOT NULL,

    exception_type VARCHAR(30) NOT NULL,

    start_at TIMESTAMPTZ NOT NULL,

    end_at TIMESTAMPTZ NOT NULL,

    reason VARCHAR(500),

    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    created_by_user_id UUID NOT NULL,

    updated_by_user_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,


    -- --------------------------------------------------------
    -- ORGANIZATION
    -- --------------------------------------------------------

    CONSTRAINT fk_availability_exception_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id),


    -- --------------------------------------------------------
    -- FACILITY
    -- --------------------------------------------------------

    CONSTRAINT fk_availability_exception_facility
        FOREIGN KEY (
            organization_id,
            facility_id
        )
        REFERENCES organization.facilities (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- PRACTITIONER
    -- --------------------------------------------------------

    CONSTRAINT fk_availability_exception_practitioner
        FOREIGN KEY (
            organization_id,
            practitioner_id
        )
        REFERENCES practitioner.practitioners (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- AUDIT ACTORS
    -- --------------------------------------------------------

    CONSTRAINT fk_availability_exception_created_by_user
        FOREIGN KEY (created_by_user_id)
        REFERENCES iam.users(id),

    CONSTRAINT fk_availability_exception_updated_by_user
        FOREIGN KEY (updated_by_user_id)
        REFERENCES iam.users(id),


    -- --------------------------------------------------------
    -- BUSINESS CONSTRAINTS
    -- --------------------------------------------------------

    CONSTRAINT ck_availability_exception_type
        CHECK (
            exception_type IN (
                'AVAILABLE',
                'UNAVAILABLE'
            )
        ),

    CONSTRAINT ck_availability_exception_time_range
        CHECK (
            start_at < end_at
        ),

    CONSTRAINT ck_availability_exception_reason_not_blank
        CHECK (
            reason IS NULL
            OR char_length(trim(reason)) > 0
        ),

    CONSTRAINT ck_availability_exception_status
        CHECK (
            status IN (
                'ACTIVE',
                'INACTIVE'
            )
        )
);


-- ============================================================
-- 5. INDEXES
-- ============================================================

CREATE INDEX idx_facility_scheduling_settings_organization
    ON scheduling.facility_scheduling_settings (
        organization_id
    );

CREATE INDEX idx_facility_scheduling_settings_facility
    ON scheduling.facility_scheduling_settings (
        facility_id
    );


CREATE INDEX idx_availability_rules_org_facility
    ON scheduling.practitioner_availability_rules (
        organization_id,
        facility_id
    );

CREATE INDEX idx_availability_rules_org_practitioner
    ON scheduling.practitioner_availability_rules (
        organization_id,
        practitioner_id
    );

CREATE INDEX idx_availability_rules_lookup
    ON scheduling.practitioner_availability_rules (
        organization_id,
        facility_id,
        practitioner_id,
        day_of_week,
        status
    );

CREATE INDEX idx_availability_rules_effective_dates
    ON scheduling.practitioner_availability_rules (
        effective_from,
        effective_to
    );


CREATE INDEX idx_availability_exceptions_org_facility
    ON scheduling.practitioner_availability_exceptions (
        organization_id,
        facility_id
    );

CREATE INDEX idx_availability_exceptions_org_practitioner
    ON scheduling.practitioner_availability_exceptions (
        organization_id,
        practitioner_id
    );

CREATE INDEX idx_availability_exceptions_lookup
    ON scheduling.practitioner_availability_exceptions (
        organization_id,
        facility_id,
        practitioner_id,
        start_at,
        end_at,
        status
    );