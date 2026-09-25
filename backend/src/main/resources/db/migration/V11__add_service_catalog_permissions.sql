-- ============================================================
-- V11
-- Add Service Catalog permissions and baseline role mappings.
--
-- ORGANIZATION_ADMIN
--   service.read
--   service.create
--   service.update
--
-- CLINIC_MANAGER
--   service.read
--   service.create
--   service.update
--
-- DOCTOR
--   service.read
--
-- NURSE
--   service.read
--
-- RECEPTIONIST
--   service.read
--
-- PHARMACIST
--   service.read
--
-- LAB_TECHNICIAN
--   service.read
-- ============================================================


-- ============================================================
-- 1. CREATE SERVICE CATALOG PERMISSIONS
-- ============================================================

INSERT INTO iam.permissions (
    id,
    code,
    name,
    description
)
VALUES
(
    '8c47a4f1-913c-4de5-80db-9c9c30f50501',
    'service.read',
    'Read Service Catalog',
    'View medical services within authorized organization scope'
),
(
    '8c47a4f1-913c-4de5-80db-9c9c30f50502',
    'service.create',
    'Create Service',
    'Create medical services within authorized organization scope'
),
(
    '8c47a4f1-913c-4de5-80db-9c9c30f50503',
    'service.update',
    'Update Service',
    'Update medical services within authorized organization scope'
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
      'service.read',
      'service.create',
      'service.update'
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
      'service.read',
      'service.create',
      'service.update'
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
  AND p.code = 'service.read'
ON CONFLICT DO NOTHING;