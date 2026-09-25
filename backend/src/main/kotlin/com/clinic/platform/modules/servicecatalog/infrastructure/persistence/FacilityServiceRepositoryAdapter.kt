package com.clinic.platform.modules.servicecatalog.infrastructure.persistence

import com.clinic.platform.modules.servicecatalog.domain.FacilityService
import com.clinic.platform.modules.servicecatalog.domain.FacilityServiceRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class FacilityServiceRepositoryAdapter(
    private val facilityServiceJpaRepository: FacilityServiceJpaRepository
) : FacilityServiceRepository {

    override fun save(
        facilityService: FacilityService
    ): FacilityService =
        facilityServiceJpaRepository
            .save(
                facilityService.toJpaEntity()
            )
            .toDomain()

    override fun findById(
        id: UUID
    ): FacilityService? =
        facilityServiceJpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()

    override fun findByOrganizationIdAndFacilityIdAndServiceId(
        organizationId: UUID,
        facilityId: UUID,
        serviceId: UUID
    ): FacilityService? =
        facilityServiceJpaRepository
            .findByOrganizationIdAndFacilityIdAndServiceId(
                organizationId,
                facilityId,
                serviceId
            )
            ?.toDomain()

    override fun existsByOrganizationIdAndFacilityIdAndServiceId(
        organizationId: UUID,
        facilityId: UUID,
        serviceId: UUID
    ): Boolean =
        facilityServiceJpaRepository
            .existsByOrganizationIdAndFacilityIdAndServiceId(
                organizationId,
                facilityId,
                serviceId
            )

    override fun findAllByOrganizationIdAndFacilityId(
        organizationId: UUID,
        facilityId: UUID
    ): List<FacilityService> =
        facilityServiceJpaRepository
            .findAllByOrganizationIdAndFacilityId(
                organizationId,
                facilityId
            )
            .map {
                it.toDomain()
            }

    override fun findAllByOrganizationIdAndServiceId(
        organizationId: UUID,
        serviceId: UUID
    ): List<FacilityService> =
        facilityServiceJpaRepository
            .findAllByOrganizationIdAndServiceId(
                organizationId,
                serviceId
            )
            .map {
                it.toDomain()
            }

    private fun FacilityService.toJpaEntity(): FacilityServiceJpaEntity =
        FacilityServiceJpaEntity(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            serviceId = serviceId,
            durationMinutes = durationMinutes,
            priceAmount = priceAmount,
            currencyCode = currencyCode,
            bookingEnabled = bookingEnabled,
            status = status,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    private fun FacilityServiceJpaEntity.toDomain(): FacilityService =
        FacilityService(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            serviceId = serviceId,
            durationMinutes = durationMinutes,
            priceAmount = priceAmount,
            currencyCode = currencyCode,
            bookingEnabled = bookingEnabled,
            status = status,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}