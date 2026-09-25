-- ============================================================
-- V14
-- Add Scheduling permissions and baseline role mappings.
--
-- ORGANIZATION_ADMIN
--   schedule.read
--   schedule.create
--   schedule.update
--
-- CLINIC_MANAGER
--   schedule.read
--   schedule.create
--   schedule.update
--
-- DOCTOR
--   schedule.read
--
-- NURSE
--   schedule.read
--
-- RECEPTIONIST
--   schedule.read
--
-- PHARMACIST
--   schedule.read
--
-- LAB_TECHNICIAN
--   schedule.read
-- ============================================================


-- ============================================================
-- 1. CREATE SCHEDULING PERMISSIONS
-- ============================================================

INSERT INTO iam.permissions (
    id,
    code,
    name,
    description
)
VALUES
(
    '8c47a4f1-913c-4de5-80db-9c9c30f50601',
    'schedule.read',
    'Read Schedule',
    'View scheduling information within authorized organization scope'
),
(
    '8c47a4f1-913c-4de5-80db-9c9c30f50602',
    'schedule.create',
    'Create Schedule',
    'Create scheduling information within authorized organization scope'
),
(
    '8c47a4f1-913c-4de5-80db-9c9c30f50603',
    'schedule.update',
    'Update Schedule',
    'Update scheduling information within authorized organization scope'
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
      'schedule.read',
      'schedule.create',
      'schedule.update'
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
      'schedule.read',
      'schedule.create',
      'schedule.update'
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
  AND p.code = 'schedule.read'
ON CONFLICT DO NOTHING;