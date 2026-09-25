package com.clinic.platform.modules.organization.infrastructure.persistence

import com.clinic.platform.modules.organization.domain.Facility
import com.clinic.platform.modules.organization.domain.FacilityRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class FacilityRepositoryAdapter(
    private val repository: FacilityJpaRepository
) : FacilityRepository {

    override fun save(
        facility: Facility
    ): Facility =
        repository
            .save(FacilityJpaEntity.fromDomain(facility))
            .toDomain()

    override fun findById(
        id: UUID
    ): Facility? =
        repository
            .findById(id)
            .orElse(null)
            ?.toDomain()

    override fun findAllByOrganizationId(
        organizationId: UUID
    ): List<Facility> =
        repository
            .findAllByOrganizationId(organizationId)
            .map { it.toDomain() }

    override fun existsByOrganizationIdAndCode(
        organizationId: UUID,
        code: String
    ): Boolean =
        repository.existsByOrganizationIdAndCode(
            organizationId,
            code
        )
}