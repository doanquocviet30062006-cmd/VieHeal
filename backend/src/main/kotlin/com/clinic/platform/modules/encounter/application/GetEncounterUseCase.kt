package com.clinic.platform.modules.encounter.application

import com.clinic.platform.modules.encounter.domain.Encounter
import com.clinic.platform.modules.encounter.domain.EncounterRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetEncounterUseCase(
    private val encounterRepository:
        EncounterRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        encounterId: UUID
    ): Encounter {

        val encounter =
            encounterRepository.findById(
                encounterId
            )
                ?: throw ResourceNotFoundException(
                    "Encounter not found: $encounterId"
                )

        if (
            encounter.organizationId != organizationId ||
            encounter.facilityId != facilityId
        ) {
            throw ResourceNotFoundException(
                "Encounter not found: $encounterId"
            )
        }

        return encounter
    }
}