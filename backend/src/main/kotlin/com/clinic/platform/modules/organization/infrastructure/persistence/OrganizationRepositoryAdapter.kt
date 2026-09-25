package com.clinic.platform.modules.organization.infrastructure.persistence

import com.clinic.platform.modules.organization.domain.Organization
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class OrganizationRepositoryAdapter(
    private val repository: OrganizationJpaRepository
) : OrganizationRepository {

    override fun save(
        organization: Organization
    ): Organization {

        val entity =
            OrganizationJpaEntity.fromDomain(organization)

        return repository
            .save(entity)
            .toDomain()
    }

    override fun findById(
        id: UUID
    ): Organization? {

        return repository
            .findById(id)
            .orElse(null)
            ?.toDomain()
    }

    override fun existsByCode(
        code: String
    ): Boolean {

        return repository.existsByCode(code)
    }
}