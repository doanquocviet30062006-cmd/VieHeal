package com.clinic.platform.modules.appointment.domain

import com.clinic.platform.shared.errors.DomainValidationException
import java.time.Instant
import java.util.UUID

data class Appointment(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val patientId: UUID,
    val practitionerId: UUID,
    val facilityServiceId: UUID,
    val scheduledStartAt: Instant,
    val scheduledEndAt: Instant,
    val status: AppointmentStatus,
    val reason: String?,
    val cancellationReason: String?,
    val createdByUserId: UUID,
    val updatedByUserId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun create(
            organizationId: UUID,
            facilityId: UUID,
            patientId: UUID,
            practitionerId: UUID,
            facilityServiceId: UUID,
            scheduledStartAt: Instant,
            scheduledEndAt: Instant,
            reason: String?,
            actorUserId: UUID
        ): Appointment {

            val normalizedReason =
                normalizeOptional(reason)

            validateTimeRange(
                scheduledStartAt = scheduledStartAt,
                scheduledEndAt = scheduledEndAt
            )

            validateReason(
                normalizedReason
            )

            val now = Instant.now()

            return Appointment(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                facilityId = facilityId,
                patientId = patientId,
                practitionerId = practitionerId,
                facilityServiceId = facilityServiceId,
                scheduledStartAt = scheduledStartAt,
                scheduledEndAt = scheduledEndAt,
                status = AppointmentStatus.SCHEDULED,
                reason = normalizedReason,
                cancellationReason = null,
                createdByUserId = actorUserId,
                updatedByUserId = actorUserId,
                createdAt = now,
                updatedAt = now
            )
        }

        private fun normalizeOptional(
            value: String?
        ): String? =
            value
                ?.trim()
                ?.takeIf { it.isNotBlank() }

        private fun validateTimeRange(
            scheduledStartAt: Instant,
            scheduledEndAt: Instant
        ) {
            if (!scheduledStartAt.isBefore(scheduledEndAt)) {
                throw DomainValidationException(
                    message =
                        "Appointment start time must be before end time",
                    code =
                        "APPOINTMENT_TIME_RANGE_INVALID"
                )
            }
        }

        private fun validateReason(
            reason: String?
        ) {
            if (
                reason != null &&
                reason.length > 1000
            ) {
                throw DomainValidationException(
                    message =
                        "Appointment reason must not exceed 1000 characters",
                    code =
                        "APPOINTMENT_REASON_TOO_LONG"
                )
            }
        }

        private fun validateCancellationReason(
            cancellationReason: String?
        ) {
            if (cancellationReason == null) {
                throw DomainValidationException(
                    message =
                        "Cancellation reason is required",
                    code =
                        "APPOINTMENT_CANCELLATION_REASON_REQUIRED"
                )
            }

            if (cancellationReason.length > 500) {
                throw DomainValidationException(
                    message =
                        "Cancellation reason must not exceed 500 characters",
                    code =
                        "APPOINTMENT_CANCELLATION_REASON_TOO_LONG"
                )
            }
        }

        private fun requireScheduled(
            status: AppointmentStatus
        ) {
            if (status != AppointmentStatus.SCHEDULED) {
                throw DomainValidationException(
                    message =
                        "Appointment must be scheduled for this operation",
                    code =
                        "APPOINTMENT_NOT_SCHEDULED"
                )
            }
        }
    }

    fun reschedule(
        scheduledStartAt: Instant,
        scheduledEndAt: Instant,
        actorUserId: UUID
    ): Appointment {

        requireScheduled(status)

        validateTimeRange(
            scheduledStartAt = scheduledStartAt,
            scheduledEndAt = scheduledEndAt
        )

        return copy(
            scheduledStartAt = scheduledStartAt,
            scheduledEndAt = scheduledEndAt,
            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }

    fun updateReason(
        reason: String?,
        actorUserId: UUID
    ): Appointment {

        requireScheduled(status)

        val normalizedReason =
            normalizeOptional(reason)

        validateReason(
            normalizedReason
        )

        return copy(
            reason = normalizedReason,
            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }

    fun cancel(
        cancellationReason: String,
        actorUserId: UUID
    ): Appointment {

        requireScheduled(status)

        val normalizedCancellationReason =
            normalizeOptional(
                cancellationReason
            )

        validateCancellationReason(
            normalizedCancellationReason
        )

        return copy(
            status = AppointmentStatus.CANCELLED,
            cancellationReason =
                normalizedCancellationReason,
            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }

    fun complete(
        actorUserId: UUID
    ): Appointment {

        requireScheduled(status)

        return copy(
            status = AppointmentStatus.COMPLETED,
            cancellationReason = null,
            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }

    fun markNoShow(
        actorUserId: UUID
    ): Appointment {

        requireScheduled(status)

        return copy(
            status = AppointmentStatus.NO_SHOW,
            cancellationReason = null,
            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }
}