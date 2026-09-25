package com.clinic.platform.modules.scheduling.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FacilitySchedulingSettingsJpaRepository :
    JpaRepository<FacilitySchedulingSettingsJpaEntity, UUID> {

    fun findByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): FacilitySchedulingSettingsJpaEntity?

    fun existsByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): Boolean
}