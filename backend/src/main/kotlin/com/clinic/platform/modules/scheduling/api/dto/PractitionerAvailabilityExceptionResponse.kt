package com.clinic.platform.modules.scheduling.api.dto

import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityException
import java.time.Instant
import java.util.UUID

data class PractitionerAvailabilityExceptionResponse(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val practitionerId: UUID,
    val exceptionType: String,
    val startAt: Instant,
    val endAt: Instant,
    val reason: String?,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun from(
            exception: PractitionerAvailabilityException
        ): PractitionerAvailabilityExceptionResponse =
            PractitionerAvailabilityExceptionResponse(
                id = exception.id,
                organizationId = exception.organizationId,
                facilityId = exception.facilityId,
                practitionerId = exception.practitionerId,
                exceptionType = exception.exceptionType.name,
                startAt = exception.startAt,
                endAt = exception.endAt,
                reason = exception.reason,
                status = exception.status.name,
                createdAt = exception.createdAt,
                updatedAt = exception.updatedAt
            )
    }
}