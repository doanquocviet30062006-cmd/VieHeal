-- ============================================================
-- V10
-- Create the Service Catalog schema.
--
-- The Service Catalog is separated from organization data
-- because it owns its own lifecycle and will later be consumed
-- by Scheduling and Appointment modules.
-- ============================================================

CREATE SCHEMA IF NOT EXISTS service_catalog;