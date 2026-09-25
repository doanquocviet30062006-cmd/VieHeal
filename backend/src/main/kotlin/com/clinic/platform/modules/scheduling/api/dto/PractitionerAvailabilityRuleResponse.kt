package com.clinic.platform.modules.scheduling.api.dto

import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityRule
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class PractitionerAvailabilityRuleResponse(
    val id: UUID,
    val organizationId: UUID,
    val facilityId: UUID,
    val practitionerId: UUID,
    val dayOfWeek: Int,
    val startLocalTime: LocalTime,
    val endLocalTime: LocalTime,
    val effectiveFrom: LocalDate,
    val effectiveTo: LocalDate?,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun from(
            rule: PractitionerAvailabilityRule
        ): PractitionerAvailabilityRuleResponse =
            PractitionerAvailabilityRuleResponse(
                id = rule.id,
                organizationId = rule.organizationId,
                facilityId = rule.facilityId,
                practitionerId = rule.practitionerId,
                dayOfWeek = rule.dayOfWeek,
                startLocalTime = rule.startLocalTime,
                endLocalTime = rule.endLocalTime,
                effectiveFrom = rule.effectiveFrom,
                effectiveTo = rule.effectiveTo,
                status = rule.status.name,
                createdAt = rule.createdAt,
                updatedAt = rule.updatedAt
            )
    }
}