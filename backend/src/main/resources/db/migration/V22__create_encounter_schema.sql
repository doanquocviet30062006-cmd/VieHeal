-- ============================================================
-- V22
-- Create the Encounter schema.
--
-- Encounter owns clinical care sessions and their core
-- clinical documentation.
--
-- Module boundaries:
--
-- - Appointment owns booking and scheduling lifecycle.
-- - Reception owns operational queue/check-in lifecycle.
-- - Encounter owns the clinical visit and EMR narrative.
-- - Diagnosis, Observation, Prescription, Lab/Imaging and
--   other clinical modules remain separate downstream modules.
--
-- Existing migrations V1-V21 are immutable.
-- ============================================================

CREATE SCHEMA IF NOT EXISTS encounter;