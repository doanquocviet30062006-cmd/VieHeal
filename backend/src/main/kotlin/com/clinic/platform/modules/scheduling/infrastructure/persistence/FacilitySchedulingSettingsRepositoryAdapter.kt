package com.clinic.platform.modules.scheduling.infrastructure.persistence

import com.clinic.platform.modules.scheduling.domain.FacilitySchedulingSettings
import com.clinic.platform.modules.scheduling.domain.FacilitySchedulingSettingsRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class FacilitySchedulingSettingsRepositoryAdapter(
    private val facilitySchedulingSettingsJpaRepository:
        FacilitySchedulingSettingsJpaRepository
) : FacilitySchedulingSettingsRepository {

    override fun save(
        settings: FacilitySchedulingSettings
    ): FacilitySchedulingSettings =
        facilitySchedulingSettingsJpaRepository
            .save(
                settings.toJpaEntity()
            )
            .toDomain()

    override fun findById(
        id: UUID
    ): FacilitySchedulingSettings? =
        facilitySchedulingSettingsJpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()

    override fun findByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): FacilitySchedulingSettings? =
        facilitySchedulingSettingsJpaRepository
            .findByOrganizationIdAndFacilityId(
                organizationId,
                facilityId
            )
            ?.toDomain()

    override fun existsByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): Boolean =
        facilitySchedulingSettingsJpaRepository
            .existsByOrganizationIdAndFacilityId(
                organizationId,
                facilityId
            )

    private fun FacilitySchedulingSettings.toJpaEntity():
        FacilitySchedulingSettingsJpaEntity =
        FacilitySchedulingSettingsJpaEntity(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            timeZoneId = timeZoneId,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    private fun FacilitySchedulingSettingsJpaEntity.toDomain():
        FacilitySchedulingSettings =
        FacilitySchedulingSettings(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            timeZoneId = timeZoneId,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}