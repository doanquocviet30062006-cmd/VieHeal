package com.clinic.platform.modules.appointment.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.UUID

interface AppointmentJpaRepository :
    JpaRepository<AppointmentJpaEntity, UUID> {

    fun findAllByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): List<AppointmentJpaEntity>

    @Query(
        value = """
            SELECT EXISTS (
                SELECT 1
                FROM appointment.appointments a
                WHERE a.organization_id = :organizationId
                  AND a.practitioner_id = :practitionerId
                  AND a.status = 'SCHEDULED'
                  AND a.scheduled_start_at < :scheduledEndAt
                  AND a.scheduled_end_at > :scheduledStartAt
            )
        """,
        nativeQuery = true
    )
    fun existsScheduledPractitionerOverlap(
        @Param("organizationId")
        organizationId: UUID,
        @Param("practitionerId")
        practitionerId: UUID,
        @Param("scheduledStartAt")
        scheduledStartAt: Instant,
        @Param("scheduledEndAt")
        scheduledEndAt: Instant
    ): Boolean

    @Query(
        value = """
            SELECT EXISTS (
                SELECT 1
                FROM appointment.appointments a
                WHERE a.organization_id = :organizationId
                  AND a.practitioner_id = :practitionerId
                  AND a.status = 'SCHEDULED'
                  AND a.id <> :excludeAppointmentId
                  AND a.scheduled_start_at < :scheduledEndAt
                  AND a.scheduled_end_at > :scheduledStartAt
            )
        """,
        nativeQuery = true
    )
    fun existsScheduledPractitionerOverlapExcluding(
        @Param("organizationId")
        organizationId: UUID,
        @Param("practitionerId")
        practitionerId: UUID,
        @Param("scheduledStartAt")
        scheduledStartAt: Instant,
        @Param("scheduledEndAt")
        scheduledEndAt: Instant,
        @Param("excludeAppointmentId")
        excludeAppointmentId: UUID
    ): Boolean

    @Query(
        value = """
            SELECT EXISTS (
                SELECT 1
                FROM appointment.appointments a
                WHERE a.organization_id = :organizationId
                  AND a.patient_id = :patientId
                  AND a.status = 'SCHEDULED'
                  AND a.scheduled_start_at < :scheduledEndAt
                  AND a.scheduled_end_at > :scheduledStartAt
            )
        """,
        nativeQuery = true
    )
    fun existsScheduledPatientOverlap(
        @Param("organizationId")
        organizationId: UUID,
        @Param("patientId")
        patientId: UUID,
        @Param("scheduledStartAt")
        scheduledStartAt: Instant,
        @Param("scheduledEndAt")
        scheduledEndAt: Instant
    ): Boolean

    @Query(
        value = """
            SELECT EXISTS (
                SELECT 1
                FROM appointment.appointments a
                WHERE a.organization_id = :organizationId
                  AND a.patient_id = :patientId
                  AND a.status = 'SCHEDULED'
                  AND a.id <> :excludeAppointmentId
                  AND a.scheduled_start_at < :scheduledEndAt
                  AND a.scheduled_end_at > :scheduledStartAt
            )
        """,
        nativeQuery = true
    )
    fun existsScheduledPatientOverlapExcluding(
        @Param("organizationId")
        organizationId: UUID,
        @Param("patientId")
        patientId: UUID,
        @Param("scheduledStartAt")
        scheduledStartAt: Instant,
        @Param("scheduledEndAt")
        scheduledEndAt: Instant,
        @Param("excludeAppointmentId")
        excludeAppointmentId: UUID
    ): Boolean
}