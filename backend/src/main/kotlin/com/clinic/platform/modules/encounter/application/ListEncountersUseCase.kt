package com.clinic.platform.modules.encounter.application

import com.clinic.platform.modules.encounter.domain.Encounter
import com.clinic.platform.modules.encounter.domain.EncounterRepository
import com.clinic.platform.modules.encounter.domain.EncounterStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListEncountersUseCase(
    private val encounterRepository:
        EncounterRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        status: EncounterStatus? = null
    ): List<Encounter> {

        return if (status == null) {
            encounterRepository
                .findAllByOrganizationIdAndFacilityId(
                    organizationId =
                        organizationId,
                    facilityId =
                        facilityId
                )
        } else {
            encounterRepository
                .findAllByOrganizationIdAndFacilityIdAndStatus(
                    organizationId =
                        organizationId,
                    facilityId =
                        facilityId,
                    status =
                        status
                )
        }
    }
}