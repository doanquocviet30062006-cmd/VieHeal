package com.clinic.platform.modules.scheduling.domain

import com.clinic.platform.shared.errors.DomainValidationException
import java.time.Instant
import java.util.UUID

data class PractitionerAvailabilityException(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val practitionerId: UUID,
    val exceptionType: AvailabilityExceptionType,
    val startAt: Instant,
    val endAt: Instant,
    val reason: String?,
    val status: AvailabilityExceptionStatus,
    val createdByUserId: UUID,
    val updatedByUserId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun create(
            organizationId: UUID,
            facilityId: UUID,
            practitionerId: UUID,
            exceptionType: AvailabilityExceptionType,
            startAt: Instant,
            endAt: Instant,
            reason: String?,
            actorUserId: UUID
        ): PractitionerAvailabilityException {

            val normalizedReason =
                normalizeOptional(reason)

            validateTimeRange(
                startAt = startAt,
                endAt = endAt
            )

            validateReason(
                normalizedReason
            )

            val now = Instant.now()

            return PractitionerAvailabilityException(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                facilityId = facilityId,
                practitionerId = practitionerId,
                exceptionType = exceptionType,
                startAt = startAt,
                endAt = endAt,
                reason = normalizedReason,
                status = AvailabilityExceptionStatus.ACTIVE,
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
            startAt: Instant,
            endAt: Instant
        ) {
            if (!startAt.isBefore(endAt)) {
                throw DomainValidationException(
                    message =
                        "Scheduling exception start time must be before end time",
                    code =
                        "SCHEDULE_EXCEPTION_TIME_RANGE_INVALID"
                )
            }
        }

        private fun validateReason(
            reason: String?
        ) {
            if (
                reason != null &&
                reason.length > 500
            ) {
                throw DomainValidationException(
                    message =
                        "Scheduling exception reason must not exceed 500 characters",
                    code =
                        "SCHEDULE_EXCEPTION_REASON_TOO_LONG"
                )
            }
        }
    }

    fun updateException(
        exceptionType: AvailabilityExceptionType,
        startAt: Instant,
        endAt: Instant,
        reason: String?,
        actorUserId: UUID
    ): PractitionerAvailabilityException {

        val normalizedReason =
            normalizeOptional(reason)

        validateTimeRange(
            startAt = startAt,
            endAt = endAt
        )

        validateReason(
            normalizedReason
        )

        return copy(
            exceptionType = exceptionType,
            startAt = startAt,
            endAt = endAt,
            reason = normalizedReason,
            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }
}