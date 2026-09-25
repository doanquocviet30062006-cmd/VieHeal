-- ============================================================
-- V13
-- Create Scheduling module schema.
--
-- Scheduling owns:
-- - facility scheduling settings
-- - practitioner recurring availability
-- - practitioner availability exceptions
--
-- Appointment booking remains a separate module.
-- ============================================================

CREATE SCHEMA IF NOT EXISTS scheduling;