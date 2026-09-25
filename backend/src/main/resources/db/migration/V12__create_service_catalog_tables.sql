-- ============================================================
-- V12
-- Create Service Catalog core tables.
--
-- Model:
--
-- service_catalog.services
--   - organization-level definition of a medical service.
--
-- service_catalog.facility_services
--   - specifies which facilities provide a service.
--   - stores facility-specific duration, price and booking state.
--
-- Tenant safety:
--   - a service belongs to exactly one organization.
--   - a facility_service must reference both a facility and a
--     service belonging to the same organization.
-- ============================================================


-- ============================================================
-- 1. SERVICES
-- ============================================================

CREATE TABLE service_catalog.services (

    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,

    service_code VARCHAR(50) NOT NULL,

    name VARCHAR(255) NOT NULL,

    description VARCHAR(1000),

    category VARCHAR(100),

    default_duration_minutes INTEGER NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_by_user_id UUID NOT NULL,

    updated_by_user_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,


    -- --------------------------------------------------------
    -- ORGANIZATION
    -- --------------------------------------------------------

    CONSTRAINT fk_service_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id),


    -- --------------------------------------------------------
    -- AUDIT ACTORS
    -- --------------------------------------------------------

    CONSTRAINT fk_service_created_by_user
        FOREIGN KEY (created_by_user_id)
        REFERENCES iam.users(id),

    CONSTRAINT fk_service_updated_by_user
        FOREIGN KEY (updated_by_user_id)
        REFERENCES iam.users(id),


    -- --------------------------------------------------------
    -- BUSINESS CONSTRAINTS
    -- --------------------------------------------------------

    CONSTRAINT uq_service_code_per_organization
        UNIQUE (
            organization_id,
            service_code
        ),

    -- Supports tenant-safe composite FK from facility_services.
    CONSTRAINT uq_services_organization_id_id
        UNIQUE (
            organization_id,
            id
        ),

    CONSTRAINT ck_service_code_not_blank
        CHECK (
            char_length(trim(service_code)) > 0
        ),

    CONSTRAINT ck_service_name_not_blank
        CHECK (
            char_length(trim(name)) > 0
        ),

    CONSTRAINT ck_service_default_duration_positive
        CHECK (
            default_duration_minutes > 0
        ),

    CONSTRAINT ck_service_category_not_blank
        CHECK (
            category IS NULL
            OR char_length(trim(category)) > 0
        ),

    CONSTRAINT ck_service_description_not_blank
        CHECK (
            description IS NULL
            OR char_length(trim(description)) > 0
        ),

    CONSTRAINT ck_service_status
        CHECK (
            status IN (
                'ACTIVE',
                'INACTIVE'
            )
        )
);


-- ============================================================
-- 2. FACILITY SERVICES
--
-- A row means:
-- "this facility provides this organization-level service".
--
-- Price and duration are stored here because different
-- facilities may configure the same service differently.
-- ============================================================

CREATE TABLE service_catalog.facility_services (

    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,

    facility_id UUID NOT NULL,

    service_id UUID NOT NULL,

    duration_minutes INTEGER NOT NULL,

    price_amount NUMERIC(12, 2) NOT NULL,

    currency_code VARCHAR(3) NOT NULL DEFAULT 'VND',

    booking_enabled BOOLEAN NOT NULL DEFAULT TRUE,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_by_user_id UUID NOT NULL,

    updated_by_user_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,


    -- --------------------------------------------------------
    -- ORGANIZATION
    -- --------------------------------------------------------

    CONSTRAINT fk_facility_service_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id),


    -- --------------------------------------------------------
    -- FACILITY
    --
    -- V7 already established:
    -- UNIQUE (organization_id, id)
    -- on organization.facilities.
    --
    -- This prevents a facility from another organization
    -- being attached here.
    -- --------------------------------------------------------

    CONSTRAINT fk_facility_service_facility
        FOREIGN KEY (
            organization_id,
            facility_id
        )
        REFERENCES organization.facilities (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- SERVICE
    --
    -- Prevents a service from another organization from being
    -- attached to this facility.
    -- --------------------------------------------------------

    CONSTRAINT fk_facility_service_service
        FOREIGN KEY (
            organization_id,
            service_id
        )
        REFERENCES service_catalog.services (
            organization_id,
            id
        ),


    -- --------------------------------------------------------
    -- AUDIT ACTORS
    -- --------------------------------------------------------

    CONSTRAINT fk_facility_service_created_by_user
        FOREIGN KEY (created_by_user_id)
        REFERENCES iam.users(id),

    CONSTRAINT fk_facility_service_updated_by_user
        FOREIGN KEY (updated_by_user_id)
        REFERENCES iam.users(id),


    -- --------------------------------------------------------
    -- BUSINESS CONSTRAINTS
    -- --------------------------------------------------------

    CONSTRAINT uq_service_per_facility
        UNIQUE (
            organization_id,
            facility_id,
            service_id
        ),

    CONSTRAINT ck_facility_service_duration_positive
        CHECK (
            duration_minutes > 0
        ),

    CONSTRAINT ck_facility_service_price_non_negative
        CHECK (
            price_amount >= 0
        ),

    CONSTRAINT ck_facility_service_currency_code
        CHECK (
            currency_code ~ '^[A-Z]{3}$'
        ),

    CONSTRAINT ck_facility_service_status
        CHECK (
            status IN (
                'ACTIVE',
                'INACTIVE'
            )
        )
);


-- ============================================================
-- 3. INDEXES - SERVICES
-- ============================================================

CREATE INDEX idx_services_organization_id
    ON service_catalog.services (
        organization_id
    );

CREATE INDEX idx_services_org_status
    ON service_catalog.services (
        organization_id,
        status
    );

CREATE INDEX idx_services_category
    ON service_catalog.services (
        category
    );


-- ============================================================
-- 4. INDEXES - FACILITY SERVICES
-- ============================================================

CREATE INDEX idx_facility_services_organization_id
    ON service_catalog.facility_services (
        organization_id
    );

CREATE INDEX idx_facility_services_facility_id
    ON service_catalog.facility_services (
        facility_id
    );

CREATE INDEX idx_facility_services_service_id
    ON service_catalog.facility_services (
        service_id
    );

CREATE INDEX idx_facility_services_org_facility_status
    ON service_catalog.facility_services (
        organization_id,
        facility_id,
        status
    );

CREATE INDEX idx_facility_services_booking
    ON service_catalog.facility_services (
        organization_id,
        facility_id,
        booking_enabled,
        status
    );