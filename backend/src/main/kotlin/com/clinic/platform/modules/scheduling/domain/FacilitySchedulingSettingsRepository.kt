package com.clinic.platform.modules.scheduling.domain

import java.util.UUID

interface FacilitySchedulingSettingsRepository {

    fun save(
        settings: FacilitySchedulingSettings
    ): FacilitySchedulingSettings

    fun findById(
        id: UUID
    ): FacilitySchedulingSettings?

    fun findByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): FacilitySchedulingSettings?

    fun existsByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): Boolean
}