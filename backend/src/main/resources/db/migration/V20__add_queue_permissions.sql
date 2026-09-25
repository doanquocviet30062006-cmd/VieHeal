-- ============================================================
-- V20
-- Add Reception / Queue permissions and baseline role mappings.
--
-- ORGANIZATION_ADMIN
--   queue.read
--   queue.create
--   queue.update
--
-- CLINIC_MANAGER
--   queue.read
--   queue.create
--   queue.update
--
-- RECEPTIONIST
--   queue.read
--   queue.create
--   queue.update
--
-- DOCTOR
--   queue.read
--   queue.update
--
-- NURSE
--   queue.read
--   queue.update
--
-- PHARMACIST
--   no Queue permission
--
-- LAB_TECHNICIAN
--   no Queue permission
-- ============================================================


-- ============================================================
-- 1. CREATE QUEUE PERMISSIONS
-- ============================================================

INSERT INTO iam.permissions (
    id,
    code,
    name,
    description
)
VALUES
(
    '8c47a4f1-913c-4de5-80db-9c9c30f50801',
    'queue.read',
    'Read Queue',
    'View reception queue entries within authorized organization scope'
),
(
    '8c47a4f1-913c-4de5-80db-9c9c30f50802',
    'queue.create',
    'Create Queue Entry',
    'Check patients into the reception queue within authorized organization scope'
),
(
    '8c47a4f1-913c-4de5-80db-9c9c30f50803',
    'queue.update',
    'Update Queue Entry',
    'Update operational queue state within authorized organization scope'
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
      'queue.read',
      'queue.create',
      'queue.update'
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
      'queue.read',
      'queue.create',
      'queue.update'
  )
ON CONFLICT DO NOTHING;


-- ============================================================
-- 4. RECEPTIONIST
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
WHERE r.code = 'RECEPTIONIST'
  AND p.code IN (
      'queue.read',
      'queue.create',
      'queue.update'
  )
ON CONFLICT DO NOTHING;


-- ============================================================
-- 5. DOCTOR / NURSE
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
      'NURSE'
  )
  AND p.code IN (
      'queue.read',
      'queue.update'
  )
ON CONFLICT DO NOTHING;