package com.clinic.platform.modules.scheduling.infrastructure.persistence

import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityRule
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityRuleRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PractitionerAvailabilityRuleRepositoryAdapter(
    private val practitionerAvailabilityRuleJpaRepository:
        PractitionerAvailabilityRuleJpaRepository
) : PractitionerAvailabilityRuleRepository {

    override fun save(
        rule: PractitionerAvailabilityRule
    ): PractitionerAvailabilityRule =
        practitionerAvailabilityRuleJpaRepository
            .save(
                rule.toJpaEntity()
            )
            .toDomain()

    override fun findById(
        id: UUID
    ): PractitionerAvailabilityRule? =
        practitionerAvailabilityRuleJpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()

    override fun findAllByOrganizationIdAndFacilityIdAndPractitionerId(
        organizationId: UUID,
        facilityId: UUID,
        practitionerId: UUID
    ): List<PractitionerAvailabilityRule> =
        practitionerAvailabilityRuleJpaRepository
            .findAllByOrganizationIdAndFacilityIdAndPractitionerId(
                organizationId,
                facilityId,
                practitionerId
            )
            .map {
                it.toDomain()
            }

    private fun PractitionerAvailabilityRule.toJpaEntity():
        PractitionerAvailabilityRuleJpaEntity =
        PractitionerAvailabilityRuleJpaEntity(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            practitionerId = practitionerId,
            dayOfWeek = dayOfWeek.toShort(),
            startLocalTime = startLocalTime,
            endLocalTime = endLocalTime,
            effectiveFrom = effectiveFrom,
            effectiveTo = effectiveTo,
            status = status,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    private fun PractitionerAvailabilityRuleJpaEntity.toDomain():
        PractitionerAvailabilityRule =
        PractitionerAvailabilityRule(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            practitionerId = practitionerId,
            dayOfWeek = dayOfWeek.toInt(),
            startLocalTime = startLocalTime,
            endLocalTime = endLocalTime,
            effectiveFrom = effectiveFrom,
            effectiveTo = effectiveTo,
            status = status,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}