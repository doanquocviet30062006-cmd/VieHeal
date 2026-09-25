package com.clinic.platform.modules.practitioner.infrastructure.persistence

import com.clinic.platform.modules.practitioner.domain.Practitioner
import com.clinic.platform.modules.practitioner.domain.PractitionerRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PractitionerRepositoryAdapter(
    private val practitionerJpaRepository: PractitionerJpaRepository
) : PractitionerRepository {

    override fun save(
        practitioner: Practitioner
    ): Practitioner {
        val entity =
            practitioner.toJpaEntity()

        return practitionerJpaRepository
            .save(entity)
            .toDomain()
    }

    override fun findById(
        id: UUID
    ): Practitioner? =
        practitionerJpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()

    override fun findByOrganizationIdAndPractitionerCode(
        organizationId: UUID,
        practitionerCode: String
    ): Practitioner? =
        practitionerJpaRepository
            .findByOrganizationIdAndPractitionerCode(
                organizationId,
                practitionerCode
            )
            ?.toDomain()

    override fun existsByOrganizationIdAndPractitionerCode(
        organizationId: UUID,
        practitionerCode: String
    ): Boolean =
        practitionerJpaRepository
            .existsByOrganizationIdAndPractitionerCode(
                organizationId,
                practitionerCode
            )

    override fun findByOrganizationIdAndMembershipId(
        organizationId: UUID,
        membershipId: UUID
    ): Practitioner? =
        practitionerJpaRepository
            .findByOrganizationIdAndMembershipId(
                organizationId,
                membershipId
            )
            ?.toDomain()

    override fun existsByOrganizationIdAndMembershipId(
        organizationId: UUID,
        membershipId: UUID
    ): Boolean =
        practitionerJpaRepository
            .existsByOrganizationIdAndMembershipId(
                organizationId,
                membershipId
            )

    override fun findAllByOrganizationId(
        organizationId: UUID
    ): List<Practitioner> =
        practitionerJpaRepository
            .findAllByOrganizationId(
                organizationId
            )
            .map {
                it.toDomain()
            }

    private fun Practitioner.toJpaEntity(): PractitionerJpaEntity =
        PractitionerJpaEntity(
            id = id,
            organizationId = organizationId,
            membershipId = membershipId,
            practitionerCode = practitionerCode,
            fullName = fullName,
            practitionerType = practitionerType,
            licenseNumber = licenseNumber,
            specialty = specialty,
            phone = phone,
            email = email,
            status = status,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    private fun PractitionerJpaEntity.toDomain(): Practitioner =
        Practitioner(
            id = id,
            organizationId = organizationId,
            membershipId = membershipId,
            practitionerCode = practitionerCode,
            fullName = fullName,
            practitionerType = practitionerType,
            licenseNumber = licenseNumber,
            specialty = specialty,
            phone = phone,
            email = email,
            status = status,
            createdByUserId = createdByUserId,
            updatedByUserId = updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}