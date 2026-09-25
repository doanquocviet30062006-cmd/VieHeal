package com.clinic.platform.modules.practitioner.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PractitionerJpaRepository :
    JpaRepository<PractitionerJpaEntity, UUID> {

    fun findByOrganizationIdAndPractitionerCode(
        organizationId: UUID,
        practitionerCode: String
    ): PractitionerJpaEntity?

    fun existsByOrganizationIdAndPractitionerCode(
        organizationId: UUID,
        practitionerCode: String
    ): Boolean

    fun findByOrganizationIdAndMembershipId(
        organizationId: UUID,
        membershipId: UUID
    ): PractitionerJpaEntity?

    fun existsByOrganizationIdAndMembershipId(
        organizationId: UUID,
        membershipId: UUID
    ): Boolean

    fun findAllByOrganizationId(
        organizationId: UUID
    ): List<PractitionerJpaEntity>
}