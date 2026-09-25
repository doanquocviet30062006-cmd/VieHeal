-- ============================================================
-- V16
-- Create Appointment module schema.
--
-- Appointment owns concrete patient bookings.
--
-- Appointment consumes:
-- - Patient
-- - Practitioner
-- - Facility Service
-- - Scheduling availability
--
-- Queue / Reception and Encounter remain separate modules.
-- ============================================================

CREATE SCHEMA IF NOT EXISTS appointment;