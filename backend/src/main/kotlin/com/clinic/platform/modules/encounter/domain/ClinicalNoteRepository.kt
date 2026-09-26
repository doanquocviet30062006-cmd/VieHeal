package com.clinic.platform.modules.encounter.domain

import java.util.UUID

interface ClinicalNoteRepository {

    fun save(
        clinicalNote: ClinicalNote
    ): ClinicalNote

    fun findById(
        id: UUID
    ): ClinicalNote?

    fun findByOrganizationIdAndEncounterId(
        organizationId: UUID,
        encounterId: UUID
    ): ClinicalNote?

    fun existsByOrganizationIdAndEncounterId(
        organizationId: UUID,
        encounterId: UUID
    ): Boolean
}