package com.clinic.platform.modules.scheduling.domain

import com.clinic.platform.shared.errors.DomainValidationException
import java.time.Instant
import java.time.ZoneId
import java.time.zone.ZoneRulesException
import java.util.UUID

data class FacilitySchedulingSettings(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val timeZoneId: String,
    val createdByUserId: UUID,
    val updatedByUserId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun create(
            organizationId: UUID,
            facilityId: UUID,
            timeZoneId: String,
            actorUserId: UUID
        ): FacilitySchedulingSettings {

            val normalizedTimeZoneId =
                timeZoneId.trim()

            validateTimeZoneId(
                normalizedTimeZoneId
            )

            val now = Instant.now()

            return FacilitySchedulingSettings(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                facilityId = facilityId,
                timeZoneId = normalizedTimeZoneId,
                createdByUserId = actorUserId,
                updatedByUserId = actorUserId,
                createdAt = now,
                updatedAt = now
            )
        }

        private fun validateTimeZoneId(
            timeZoneId: String
        ) {
            if (timeZoneId.isBlank()) {
                throw DomainValidationException(
                    message =
                        "Facility scheduling time zone must not be blank",
                    code =
                        "SCHEDULE_TIME_ZONE_BLANK"
                )
            }

            if (timeZoneId.length > 100) {
                throw DomainValidationException(
                    message =
                        "Facility scheduling time zone must not exceed 100 characters",
                    code =
                        "SCHEDULE_TIME_ZONE_TOO_LONG"
                )
            }

            try {
                ZoneId.of(timeZoneId)
            } catch (_: ZoneRulesException) {
                throw DomainValidationException(
                    message =
                        "Facility scheduling time zone is invalid",
                    code =
                        "SCHEDULE_TIME_ZONE_INVALID"
                )
            } catch (_: IllegalArgumentException) {
                throw DomainValidationException(
                    message =
                        "Facility scheduling time zone is invalid",
                    code =
                        "SCHEDULE_TIME_ZONE_INVALID"
                )
            }
        }
    }

    fun updateTimeZone(
        timeZoneId: String,
        actorUserId: UUID
    ): FacilitySchedulingSettings {

        val normalizedTimeZoneId =
            timeZoneId.trim()

        validateTimeZoneId(
            normalizedTimeZoneId
        )

        return copy(
            timeZoneId = normalizedTimeZoneId,
            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }
}