-- ============================================================
-- V6
-- Add Patient module permissions and baseline role mappings.
--
-- Separation of duties:
--
-- ORGANIZATION_ADMIN
--   patient.read
--   patient.create
--   patient.update
--
-- DOCTOR
--   patient.read
--   patient.update
--
-- NURSE
--   patient.read
--   patient.update
--
-- RECEPTIONIST
--   patient.read
--   patient.create
--
-- Notes:
-- - Patient permissions here apply to the patient profile/module.
-- - Clinical notes, diagnoses, observations, orders, etc. will
--   receive their own permissions in their own modules later.
-- ============================================================


-- ============================================================
-- 1. CREATE PATIENT PERMISSIONS
-- ============================================================

INSERT INTO iam.permissions (
    id,
    code,
    name,
    description
)
VALUES
(
    '6d6403d7-b87b-4ce1-b469-a61838f50401',
    'patient.read',
    'Read Patient',
    'View patient profiles within authorized scope'
),
(
    '6d6403d7-b87b-4ce1-b469-a61838f50402',
    'patient.create',
    'Create Patient',
    'Create patient profiles within authorized scope'
),
(
    '6d6403d7-b87b-4ce1-b469-a61838f50403',
    'patient.update',
    'Update Patient',
    'Update patient profiles within authorized scope'
)
ON CONFLICT DO NOTHING;


-- ============================================================
-- 2. ORGANIZATION_ADMIN
--
-- patient.read
-- patient.create
-- patient.update
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
      'patient.read',
      'patient.create',
      'patient.update'
  )
ON CONFLICT DO NOTHING;


-- ============================================================
-- 3. DOCTOR
--
-- patient.read
-- patient.update
--
-- Intentionally does NOT receive patient.create.
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
WHERE r.code = 'DOCTOR'
  AND p.code IN (
      'patient.read',
      'patient.update'
  )
ON CONFLICT DO NOTHING;


-- ============================================================
-- 4. NURSE
--
-- patient.read
-- patient.update
--
-- Intentionally does NOT receive patient.create.
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
WHERE r.code = 'NURSE'
  AND p.code IN (
      'patient.read',
      'patient.update'
  )
ON CONFLICT DO NOTHING;


-- ============================================================
-- 5. RECEPTIONIST
--
-- patient.read
-- patient.create
--
-- Intentionally does NOT receive patient.update.
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
      'patient.read',
      'patient.create'
  )
ON CONFLICT DO NOTHING;