INSERT INTO iam.roles (
    id,
    code,
    name,
    description,
    scope
)
VALUES
(
    '00000000-0000-0000-0000-000000000001',
    'SYSTEM_ADMIN',
    'System Administrator',
    'Platform-wide administration',
    'SYSTEM'
),
(
    '00000000-0000-0000-0000-000000000002',
    'ORGANIZATION_ADMIN',
    'Organization Administrator',
    'Administration within one healthcare organization',
    'ORGANIZATION'
),
(
    '00000000-0000-0000-0000-000000000003',
    'CLINIC_MANAGER',
    'Clinic Manager',
    'Clinic operational management',
    'ORGANIZATION'
),
(
    '00000000-0000-0000-0000-000000000004',
    'DOCTOR',
    'Doctor',
    'Clinical practitioner',
    'ORGANIZATION'
),
(
    '00000000-0000-0000-0000-000000000005',
    'NURSE',
    'Nurse',
    'Nursing staff',
    'ORGANIZATION'
),
(
    '00000000-0000-0000-0000-000000000006',
    'RECEPTIONIST',
    'Receptionist',
    'Reception and appointment operations',
    'ORGANIZATION'
),
(
    '00000000-0000-0000-0000-000000000007',
    'PHARMACIST',
    'Pharmacist',
    'Pharmacy operations',
    'ORGANIZATION'
),
(
    '00000000-0000-0000-0000-000000000008',
    'LAB_TECHNICIAN',
    'Laboratory Technician',
    'Laboratory operations',
    'ORGANIZATION'
);


INSERT INTO iam.permissions (
    id,
    code,
    name
)
VALUES
(
    '10000000-0000-0000-0000-000000000001',
    'organization.read',
    'Read organization'
),
(
    '10000000-0000-0000-0000-000000000002',
    'organization.manage',
    'Manage organization'
),
(
    '10000000-0000-0000-0000-000000000003',
    'facility.read',
    'Read facilities'
),
(
    '10000000-0000-0000-0000-000000000004',
    'facility.manage',
    'Manage facilities'
),
(
    '10000000-0000-0000-0000-000000000005',
    'iam.user.read',
    'Read users'
),
(
    '10000000-0000-0000-0000-000000000006',
    'iam.user.manage',
    'Manage users'
),
(
    '10000000-0000-0000-0000-000000000007',
    'iam.role.manage',
    'Manage roles'
);

INSERT INTO iam.role_permissions (
    role_id,
    permission_id
)
VALUES
(
    '00000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000001'
),
(
    '00000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000002'
),
(
    '00000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000003'
),
(
    '00000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000004'
),
(
    '00000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000005'
),
(
    '00000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000006'
),
(
    '00000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000007'
);