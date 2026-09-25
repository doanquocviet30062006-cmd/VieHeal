package com.clinic.platform.modules.appointment.api.dto

import com.clinic.platform.modules.appointment.domain.Appointment
import java.time.Instant
import java.util.UUID

data class AppointmentResponse(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val patientId: UUID,
    val practitionerId: UUID,
    val facilityServiceId: UUID,
    val scheduledStartAt: Instant,
    val scheduledEndAt: Instant,
    val status: String,
    val reason: String?,
    val cancellationReason: String?,
    val createdByUserId: UUID,
    val updatedByUserId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun from(
            appointment: Appointment
        ): AppointmentResponse =
            AppointmentResponse(
                id = appointment.id,
                organizationId = appointment.organizationId,
                facilityId = appointment.facilityId,
                patientId = appointment.patientId,
                practitionerId = appointment.practitionerId,
                facilityServiceId = appointment.facilityServiceId,
                scheduledStartAt = appointment.scheduledStartAt,
                scheduledEndAt = appointment.scheduledEndAt,
                status = appointment.status.name,
                reason = appointment.reason,
                cancellationReason = appointment.cancellationReason,
                createdByUserId = appointment.createdByUserId,
                updatedByUserId = appointment.updatedByUserId,
                createdAt = appointment.createdAt,
                updatedAt = appointment.updatedAt
            )
    }
}