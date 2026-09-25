package com.clinic.platform.modules.organization.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FacilityJpaRepository :
    JpaRepository<FacilityJpaEntity, UUID> {

    fun findAllByOrganizationId(
        organizationId: UUID
    ): List<FacilityJpaEntity>

    fun existsByOrganizationIdAndCode(
        organizationId: UUID,
        code: String
    ): Boolean
}