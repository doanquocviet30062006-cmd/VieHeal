package com.clinic.platform.modules.encounter.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ClinicalNoteJpaRepository :
    JpaRepository<ClinicalNoteJpaEntity, UUID> {

    fun findByOrganizationIdAndEncounterId(
        organizationId: UUID,
        encounterId: UUID
    ): ClinicalNoteJpaEntity?

    fun existsByOrganizationIdAndEncounterId(
        organizationId: UUID,
        encounterId: UUID
    ): Boolean
}