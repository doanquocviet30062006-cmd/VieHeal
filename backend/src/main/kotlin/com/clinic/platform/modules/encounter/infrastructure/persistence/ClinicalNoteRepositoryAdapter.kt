package com.clinic.platform.modules.encounter.infrastructure.persistence

import com.clinic.platform.modules.encounter.domain.ClinicalNote
import com.clinic.platform.modules.encounter.domain.ClinicalNoteRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ClinicalNoteRepositoryAdapter(
    private val jpaRepository:
        ClinicalNoteJpaRepository
) : ClinicalNoteRepository {

    override fun save(
        clinicalNote: ClinicalNote
    ): ClinicalNote =
        jpaRepository
            .save(
                clinicalNote.toJpaEntity()
            )
            .toDomain()

    override fun findById(
        id: UUID
    ): ClinicalNote? =
        jpaRepository
            .findById(id)
            .orElse(null)
            ?.toDomain()

    override fun findByOrganizationIdAndEncounterId(
        organizationId: UUID,
        encounterId: UUID
    ): ClinicalNote? =
        jpaRepository
            .findByOrganizationIdAndEncounterId(
                organizationId,
                encounterId
            )
            ?.toDomain()

    override fun existsByOrganizationIdAndEncounterId(
        organizationId: UUID,
        encounterId: UUID
    ): Boolean =
        jpaRepository
            .existsByOrganizationIdAndEncounterId(
                organizationId,
                encounterId
            )

    private fun ClinicalNote.toJpaEntity():
        ClinicalNoteJpaEntity =
        ClinicalNoteJpaEntity(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            encounterId = encounterId,
            chiefComplaint =
                chiefComplaint,
            subjective = subjective,
            objective = objective,
            assessment = assessment,
            plan = plan,
            createdByUserId =
                createdByUserId,
            updatedByUserId =
                updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    private fun ClinicalNoteJpaEntity.toDomain():
        ClinicalNote =
        ClinicalNote(
            id = id,
            organizationId = organizationId,
            facilityId = facilityId,
            encounterId = encounterId,
            chiefComplaint =
                chiefComplaint,
            subjective = subjective,
            objective = objective,
            assessment = assessment,
            plan = plan,
            createdByUserId =
                createdByUserId,
            updatedByUserId =
                updatedByUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}