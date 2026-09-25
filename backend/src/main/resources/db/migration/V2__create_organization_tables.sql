CREATE TABLE organization.organizations (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_organization_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))
);

CREATE TABLE organization.facilities (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address_line VARCHAR(500),
    ward VARCHAR(150),
    district VARCHAR(150),
    province VARCHAR(150),
    country_code VARCHAR(2) NOT NULL DEFAULT 'VN',
    phone VARCHAR(30),
    email VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_facility_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization.organizations(id),

    CONSTRAINT uq_facility_code
        UNIQUE (organization_id, code),

    CONSTRAINT ck_facility_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))
);

CREATE INDEX idx_facility_organization_id
    ON organization.facilities(organization_id);