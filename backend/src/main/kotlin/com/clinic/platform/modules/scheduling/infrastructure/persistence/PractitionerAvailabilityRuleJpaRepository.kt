package com.clinic.platform.modules.scheduling.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PractitionerAvailabilityRuleJpaRepository :
    JpaRepository<PractitionerAvailabilityRuleJpaEntity, UUID> {

    fun findAllByOrganizationIdAndFacilityIdAndPractitionerId(
        organizationId: UUID,
        facilityId: UUID,
        practitionerId: UUID
    ): List<PractitionerAvailabilityRuleJpaEntity>
}