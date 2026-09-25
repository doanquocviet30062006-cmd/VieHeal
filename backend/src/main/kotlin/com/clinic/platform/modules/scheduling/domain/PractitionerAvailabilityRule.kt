package com.clinic.platform.modules.scheduling.domain

import com.clinic.platform.shared.errors.DomainValidationException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class PractitionerAvailabilityRule(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val practitionerId: UUID,
    val dayOfWeek: Int,
    val startLocalTime: LocalTime,
    val endLocalTime: LocalTime,
    val effectiveFrom: LocalDate,
    val effectiveTo: LocalDate?,
    val status: AvailabilityRuleStatus,
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
            dayOfWeek: Int,
            startLocalTime: LocalTime,
            endLocalTime: LocalTime,
            effectiveFrom: LocalDate,
            effectiveTo: LocalDate?,
            actorUserId: UUID
        ): PractitionerAvailabilityRule {

            validateDayOfWeek(
                dayOfWeek
            )

            validateTimeRange(
                startLocalTime = startLocalTime,
                endLocalTime = endLocalTime
            )

            validateEffectiveRange(
                effectiveFrom = effectiveFrom,
                effectiveTo = effectiveTo
            )

            val now = Instant.now()

            return PractitionerAvailabilityRule(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                facilityId = facilityId,
                practitionerId = practitionerId,
                dayOfWeek = dayOfWeek,
                startLocalTime = startLocalTime,
                endLocalTime = endLocalTime,
                effectiveFrom = effectiveFrom,
                effectiveTo = effectiveTo,
                status = AvailabilityRuleStatus.ACTIVE,
                createdByUserId = actorUserId,
                updatedByUserId = actorUserId,
                createdAt = now,
                updatedAt = now
            )
        }

        private fun validateDayOfWeek(
            dayOfWeek: Int
        ) {
            if (dayOfWeek !in 1..7) {
                throw DomainValidationException(
                    message =
                        "Scheduling day of week must be between 1 and 7",
                    code =
                        "SCHEDULE_DAY_OF_WEEK_INVALID"
                )
            }
        }

        private fun validateTimeRange(
            startLocalTime: LocalTime,
            endLocalTime: LocalTime
        ) {
            if (!startLocalTime.isBefore(endLocalTime)) {
                throw DomainValidationException(
                    message =
                        "Scheduling start time must be before end time",
                    code =
                        "SCHEDULE_TIME_RANGE_INVALID"
                )
            }
        }

        private fun validateEffectiveRange(
            effectiveFrom: LocalDate,
            effectiveTo: LocalDate?
        ) {
            if (
                effectiveTo != null &&
                effectiveTo.isBefore(effectiveFrom)
            ) {
                throw DomainValidationException(
                    message =
                        "Scheduling effective end date must not be before start date",
                    code =
                        "SCHEDULE_EFFECTIVE_RANGE_INVALID"
                )
            }
        }
    }

    fun updateSchedule(
        dayOfWeek: Int,
        startLocalTime: LocalTime,
        endLocalTime: LocalTime,
        effectiveFrom: LocalDate,
        effectiveTo: LocalDate?,
        actorUserId: UUID
    ): PractitionerAvailabilityRule {

        validateDayOfWeek(
            dayOfWeek
        )

        validateTimeRange(
            startLocalTime = startLocalTime,
            endLocalTime = endLocalTime
        )

        validateEffectiveRange(
            effectiveFrom = effectiveFrom,
            effectiveTo = effectiveTo
        )

        return copy(
            dayOfWeek = dayOfWeek,
            startLocalTime = startLocalTime,
            endLocalTime = endLocalTime,
            effectiveFrom = effectiveFrom,
            effectiveTo = effectiveTo,
            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }
}