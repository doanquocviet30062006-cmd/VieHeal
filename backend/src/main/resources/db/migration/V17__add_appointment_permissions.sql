-- ============================================================
-- V17
-- Add Appointment permissions and baseline role mappings.
--
-- ORGANIZATION_ADMIN
--   appointment.read
--   appointment.create
--   appointment.update
--
-- CLINIC_MANAGER
--   appointment.read
--   appointment.create
--   appointment.update
--
-- RECEPTIONIST
--   appointment.read
--   appointment.create
--   appointment.update
--
-- DOCTOR
--   appointment.read
--
-- NURSE
--   appointment.read
--
-- PHARMACIST
--   no Appointment permission
--
-- LAB_TECHNICIAN
--   no Appointment permission
-- ============================================================


-- ============================================================
-- 1. CREATE APPOINTMENT PERMISSIONS
-- ============================================================

INSERT INTO iam.permissions (
    id,
    code,
    name,
    description
)
VALUES
(
    '8c47a4f1-913c-4de5-80db-9c9c30f50701',
    'appointment.read',
    'Read Appointment',
    'View appointments within authorized organization scope'
),
(
    '8c47a4f1-913c-4de5-80db-9c9c30f50702',
    'appointment.create',
    'Create Appointment',
    'Create appointments within authorized organization scope'
),
(
    '8c47a4f1-913c-4de5-80db-9c9c30f50703',
    'appointment.update',
    'Update Appointment',
    'Update, reschedule, or cancel appointments within authorized organization scope'
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
      'appointment.read',
      'appointment.create',
      'appointment.update'
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
      'appointment.read',
      'appointment.create',
      'appointment.update'
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
      'appointment.read',
      'appointment.create',
      'appointment.update'
  )
ON CONFLICT DO NOTHING;


-- ============================================================
-- 5. READ-ONLY CLINICAL ROLES
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
  AND p.code = 'appointment.read'
ON CONFLICT DO NOTHING;