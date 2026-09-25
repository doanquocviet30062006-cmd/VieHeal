-- ============================================================
-- V5
-- Grant baseline read permissions to DOCTOR.
--
-- DOCTOR may:
--   - read organization information
--   - read assigned facility information
--
-- DOCTOR may NOT:
--   - manage organizations
--   - manage facilities
--   - manage IAM users
--   - manage roles
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
      'organization.read',
      'facility.read'
  )
ON CONFLICT DO NOTHING;