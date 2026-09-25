-- ============================================================
-- V8
-- Add Practitioner module permissions and baseline role mappings.
--
-- ORGANIZATION_ADMIN
--   practitioner.read
--   practitioner.create
--   practitioner.update
--
-- CLINIC_MANAGER
--   practitioner.read
--   practitioner.create
--   practitioner.update
--
-- DOCTOR
--   practitioner.read
--
-- NURSE
--   practitioner.read
--
-- RECEPTIONIST
--   practitioner.read
--
-- PHARMACIST
--   practitioner.read
--
-- LAB_TECHNICIAN
--   practitioner.read
-- ============================================================


-- ============================================================
-- 1. CREATE PRACTITIONER PERMISSIONS
-- ============================================================

INSERT INTO iam.permissions (
    id,
    code,
    name,
    description
)
VALUES
(
    '7a9f83d2-6b8d-4d23-a4c4-947c13ab0401',
    'practitioner.read',
    'Read Practitioner',
    'View practitioner profiles within authorized organization scope'
),
(
    '7a9f83d2-6b8d-4d23-a4c4-947c13ab0402',
    'practitioner.create',
    'Create Practitioner',
    'Create practitioner profiles within authorized organization scope'
),
(
    '7a9f83d2-6b8d-4d23-a4c4-947c13ab0403',
    'practitioner.update',
    'Update Practitioner',
    'Update practitioner profiles within authorized organization scope'
)
ON CONFLICT DO NOTHING;


-- ============================================================
-- 2. ORGANIZATION_ADMIN
-- ============================================================

INSERT INTO iam.role_permissions (
    role_id,
    permission_id
)
SELECT
    r.id,
    p.id
FROM iam.roles r
CROSS JOIN iam.permissions p
WHERE r.code = 'ORGANIZATION_ADMIN'
  AND p.code IN (
      'practitioner.read',
      'practitioner.create',
      'practitioner.update'
  )
ON CONFLICT DO NOTHING;


-- ============================================================
-- 3. CLINIC_MANAGER
-- ============================================================

INSERT INTO iam.role_permissions (
    role_id,
    permission_id
)
SELECT
    r.id,
    p.id
FROM iam.roles r
CROSS JOIN iam.permissions p
WHERE r.code = 'CLINIC_MANAGER'
  AND p.code IN (
      'practitioner.read',
      'practitioner.create',
      'practitioner.update'
  )
ON CONFLICT DO NOTHING;


-- ============================================================
-- 4. READ-ONLY CLINICAL / OPERATIONAL ROLES
-- ============================================================

INSERT INTO iam.role_permissions (
    role_id,
    permission_id
)
SELECT
    r.id,
    p.id
FROM iam.roles r
CROSS JOIN iam.permissions p
WHERE r.code IN (
      'DOCTOR',
      'NURSE',
      'RECEPTIONIST',
      'PHARMACIST',
      'LAB_TECHNICIAN'
  )
  AND p.code = 'practitioner.read'
ON CONFLICT DO NOTHING;