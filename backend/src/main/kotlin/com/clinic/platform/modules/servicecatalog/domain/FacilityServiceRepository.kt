package com.clinic.platform.modules.servicecatalog.domain

import java.util.UUID

interface FacilityServiceRepository {

    fun save(
        facilityService: FacilityService
    ): FacilityService

    fun findById(
        id: UUID
    ): FacilityService?

    fun findByOrganizationIdAndFacilityIdAndServiceId(
        organizationId: UUID,
        facilityId: UUID,
        serviceId: UUID
    ): FacilityService?

    fun existsByOrganizationIdAndFacilityIdAndServiceId(
        organizationId: UUID,
        facilityId: UUID,
        serviceId: UUID
    ): Boolean

    fun findAllByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): List<FacilityService>

    fun findAllByOrganizationIdAndServiceId(
        organizationId: UUID,
        serviceId: UUID
    ): List<FacilityService>
}