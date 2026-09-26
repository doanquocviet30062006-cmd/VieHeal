-- ============================================================
-- V23
-- Add Encounter / EMR permissions and baseline role mappings.
--
-- Separation of duties:
--
-- ORGANIZATION_ADMIN
--   encounter.read
--
-- CLINIC_MANAGER
--   encounter.read
--
-- DOCTOR
--   encounter.read
--   encounter.create
--   encounter.update
--   clinical_note.read
--   clinical_note.update
--
-- NURSE
--   encounter.read
--   clinical_note.read
--
-- RECEPTIONIST
--   no Encounter / clinical-note permissions
--
-- PHARMACIST
--   no Encounter / clinical-note permissions
--
-- LAB_TECHNICIAN
--   no Encounter / clinical-note permissions
--
-- Notes:
-- - encounter.read exposes Encounter metadata/status.
-- - Clinical narrative is protected by separate clinical_note
--   permissions.
-- - Diagnosis, Observation, Prescription and Lab/Imaging will
--   receive their own permissions in later modules.
-- - SYSTEM_ADMIN is intentionally not granted clinical access
--   by this migration.
-- ============================================================


-- ============================================================
-- 1. CREATE ENCOUNTER / CLINICAL NOTE PERMISSIONS
-- ============================================================

INSERT INTO iam.permissions (
    id,
    code,
    name,
    description
)
VALUES
(
    '2f5f6d52-59bc-4cf8-9c7b-3e7f1fcd2201',
    'encounter.read',
    'Read Encounter',
    'View encounter metadata within authorized scope'
),
(
    '2f5f6d52-59bc-4cf8-9c7b-3e7f1fcd2202',
    'encounter.create',
    'Create Encounter',
    'Start clinical encounters within authorized scope'
),
(
    '2f5f6d52-59bc-4cf8-9c7b-3e7f1fcd2203',
    'encounter.update',
    'Update Encounter',
    'Update encounter lifecycle within authorized scope'
),
(
    '2f5f6d52-59bc-4cf8-9c7b-3e7f1fcd2204',
    'clinical_note.read',
    'Read Clinical Note',
    'View clinical encounter notes within authorized scope'
),
(
    '2f5f6d52-59bc-4cf8-9c7b-3e7f1fcd2205',
    'clinical_note.update',
    'Update Clinical Note',
    'Create or update clinical encounter notes within authorized scope'
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
      'encounter.read'
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
      'encounter.read'
  )
ON CONFLICT DO NOTHING;


-- ============================================================
-- 4. DOCTOR
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
      'encounter.read',
      'encounter.create',
      'encounter.update',
      'clinical_note.read',
      'clinical_note.update'
  )
ON CONFLICT DO NOTHING;


-- ============================================================
-- 5. NURSE
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
      'encounter.read',
      'clinical_note.read'
  )
ON CONFLICT DO NOTHING;


-- ============================================================
-- 6. RECEPTIONIST / PHARMACIST / LAB_TECHNICIAN
--
-- Intentionally receive no Encounter or clinical-note
-- permissions from this migration.
-- ============================================================