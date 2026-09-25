package com.clinic.platform.modules.appointment.domain

import java.time.Instant
import java.util.UUID

interface AppointmentRepository {

    fun save(
        appointment: Appointment
    ): Appointment

    fun findById(
        id: UUID
    ): Appointment?

    fun findAllByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): List<Appointment>

    fun existsScheduledPractitionerOverlap(
        organizationId: UUID,
        practitionerId: UUID,
        scheduledStartAt: Instant,
        scheduledEndAt: Instant,
        excludeAppointmentId: UUID? = null
    ): Boolean

    fun existsScheduledPatientOverlap(
        organizationId: UUID,
        patientId: UUID,
        scheduledStartAt: Instant,
        scheduledEndAt: Instant,
        excludeAppointmentId: UUID? = null
    ): Boolean
}