package com.clinic.platform.modules.iam.infrastructure.persistence

import com.clinic.platform.modules.iam.domain.OrganizationMembership
import com.clinic.platform.modules.iam.domain.OrganizationMembershipRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class OrganizationMembershipRepositoryAdapter(
    private val jpaRepository: OrganizationMembershipJpaRepository
) : OrganizationMembershipRepository {

    override fun save(
        membership: OrganizationMembership
    ): OrganizationMembership {

        return jpaRepository
            .save(
                OrganizationMembershipJpaEntity.fromDomain(
                    membership
                )
            )
            .toDomain()
    }

    override fun findById(
        id: UUID
    ): OrganizationMembership? {

        return jpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()
    }

    override fun findAllByUserId(
        userId: UUID
    ): List<OrganizationMembership> {

        return jpaRepository
            .findAllByUserId(userId)
            .map { it.toDomain() }
    }

    override fun findAllByOrganizationId(
        organizationId: UUID
    ): List<OrganizationMembership> {

        return jpaRepository
            .findAllByOrganizationId(organizationId)
            .map { it.toDomain() }
    }

    override fun findByUserIdAndOrganizationId(
        userId: UUID,
        organizationId: UUID
    ): OrganizationMembership? {

        return jpaRepository
            .findByUserIdAndOrganizationId(
                userId,
                organizationId
            )
            ?.toDomain()
    }

    override fun existsByUserIdAndOrganizationId(
        userId: UUID,
        organizationId: UUID
    ): Boolean {

        return jpaRepository
            .existsByUserIdAndOrganizationId(
                userId,
                organizationId
            )
    }
}