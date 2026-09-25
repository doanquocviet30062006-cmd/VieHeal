package com.clinic.platform.modules.servicecatalog.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FacilityServiceJpaRepository :
    JpaRepository<FacilityServiceJpaEntity, UUID> {

    fun findByOrganizationIdAndFacilityIdAndServiceId(
        organizationId: UUID,
        facilityId: UUID,
        serviceId: UUID
    ): FacilityServiceJpaEntity?

    fun existsByOrganizationIdAndFacilityIdAndServiceId(
        organizationId: UUID,
        facilityId: UUID,
        serviceId: UUID
    ): Boolean

    fun findAllByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): List<FacilityServiceJpaEntity>

    fun findAllByOrganizationIdAndServiceId(
        organizationId: UUID,
        serviceId: UUID
    ): List<FacilityServiceJpaEntity>
}