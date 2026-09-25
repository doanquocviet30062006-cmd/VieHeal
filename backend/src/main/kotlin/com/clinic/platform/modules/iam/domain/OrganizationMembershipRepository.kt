package com.clinic.platform.modules.iam.domain

import java.util.UUID

interface OrganizationMembershipRepository {

    fun save(
        membership: OrganizationMembership
    ): OrganizationMembership

    fun findById(
        id: UUID
    ): OrganizationMembership?

    fun findAllByUserId(
        userId: UUID
    ): List<OrganizationMembership>

    fun findAllByOrganizationId(
        organizationId: UUID
    ): List<OrganizationMembership>

    fun findByUserIdAndOrganizationId(
        userId: UUID,
        organizationId: UUID
    ): OrganizationMembership?

    fun existsByUserIdAndOrganizationId(
        userId: UUID,
        organizationId: UUID
    ): Boolean
}