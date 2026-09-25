package com.clinic.platform.modules.scheduling.application

import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.practitioner.domain.PractitionerRepository
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityRule
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityRuleRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListPractitionerAvailabilityRulesUseCase(
    private val facilityRepository: FacilityRepository,
    private val practitionerRepository: PractitionerRepository,
    private val practitionerAvailabilityRuleRepository:
        PractitionerAvailabilityRuleRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        practitionerId: UUID
    ): List<PractitionerAvailabilityRule> {

        val facility =
            facilityRepository.findById(facilityId)
                ?: throw ResourceNotFoundException(
                    "Facility not found in organization: $facilityId"
                )

        if (facility.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Facility not found in organization: $facilityId"
            )
        }

        val practitioner =
            practitionerRepository.findById(practitionerId)
                ?: throw ResourceNotFoundException(
                    "Practitioner not found in organization: $practitionerId"
                )

        if (practitioner.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Practitioner not found in organization: $practitionerId"
            )
        }

        return practitionerAvailabilityRuleRepository
            .findAllByOrganizationIdAndFacilityIdAndPractitionerId(
                organizationId,
                facilityId,
                practitionerId
            )
    }
}