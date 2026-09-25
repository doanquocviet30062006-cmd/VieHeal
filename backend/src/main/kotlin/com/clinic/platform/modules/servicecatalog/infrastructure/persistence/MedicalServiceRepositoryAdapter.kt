package com.clinic.platform.modules.servicecatalog.infrastructure.persistence

import com.clinic.platform.modules.servicecatalog.domain.MedicalService
import com.clinic.platform.modules.servicecatalog.domain.MedicalServiceRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class MedicalServiceRepositoryAdapter(
    private val medicalServiceJpaRepository: MedicalServiceJpaRepository
) : MedicalServiceRepository {

    override fun save(
        service: MedicalService
    ): MedicalService =
        medicalServiceJpaRepository
            .save(
                service.toJpaEntity()
            )
            .toDomain()

    override fun findById(
        id: UUID
    ): MedicalService? =
        medicalServiceJpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()

    override fun findByOrganizationIdAndServiceCode(
        organizationId: UUID,
        serviceCode: String
    ): MedicalService? =
        medicalServiceJpaRepository
            .findByOrganizationIdAndServiceCode(
                organizationId,
                serviceCode
            )
            ?.toDomain()

    override fun existsByOrganizationIdAndServiceCode(
        organizationId: UUID,
        serviceCode: String
    ): Boolean =
        medicalServiceJpaRepository
            .existsByOrganizationIdAndServiceCode(
                organizationId,
                serviceCode
            )

    override fun findAllByOrganizationId(
        organizationId: UUID
    ): List<MedicalService> =
        medicalServiceJpaRepository
            .findAllByOrganizationId(
                organizationId
            )
            .map {
                it.toDomain()
            }

    private fun MedicalService.toJpaEntity(): MedicalServiceJpaEntity =
        MedicalServiceJpaEntity(
            id = id,
            organizationId = organizationId,
            serviceCode = serviceCode,
            name = name,
            description = description,
            category = category,
            defaultDurationMinutes = defaultDurationMinutes,
            status = status,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    private fun MedicalServiceJpaEntity.toDomain(): MedicalService =
        MedicalService(
            id = id,
            organizationId = organizationId,
            serviceCode = serviceCode,
            name = name,
            description = description,
            category = category,
            defaultDurationMinutes = defaultDurationMinutes,
            status = status,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}