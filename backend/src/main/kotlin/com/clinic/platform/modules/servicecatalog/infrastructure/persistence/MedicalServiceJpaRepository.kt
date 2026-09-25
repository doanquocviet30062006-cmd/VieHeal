package com.clinic.platform.modules.servicecatalog.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MedicalServiceJpaRepository :
    JpaRepository<MedicalServiceJpaEntity, UUID> {

    fun findByOrganizationIdAndServiceCode(
        organizationId: UUID,
        serviceCode: String
    ): MedicalServiceJpaEntity?

    fun existsByOrganizationIdAndServiceCode(
        organizationId: UUID,
        serviceCode: String
    ): Boolean

    fun findAllByOrganizationId(
        organizationId: UUID
    ): List<MedicalServiceJpaEntity>
}