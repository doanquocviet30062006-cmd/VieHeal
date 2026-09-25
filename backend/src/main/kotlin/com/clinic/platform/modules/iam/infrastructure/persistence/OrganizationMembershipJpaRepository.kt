package com.clinic.platform.modules.iam.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrganizationMembershipJpaRepository :
    JpaRepository<OrganizationMembershipJpaEntity, UUID> {

    fun findAllByUserId(
        userId: UUID
    ): List<OrganizationMembershipJpaEntity>

    fun findAllByOrganizationId(
        organizationId: UUID
    ): List<OrganizationMembershipJpaEntity>

    fun findByUserIdAndOrganizationId(
        userId: UUID,
        organizationId: UUID
    ): OrganizationMembershipJpaEntity?

    fun existsByUserIdAndOrganizationId(
        userId: UUID,
        organizationId: UUID
    ): Boolean
}