-- ============================================================
-- V19
-- Create Reception / Queue module schema.
--
-- Reception owns operational patient check-in and queue state.
--
-- Appointment remains the owner of scheduled bookings.
-- Encounter remains the owner of clinical care documentation.
-- ============================================================

CREATE SCHEMA IF NOT EXISTS reception;