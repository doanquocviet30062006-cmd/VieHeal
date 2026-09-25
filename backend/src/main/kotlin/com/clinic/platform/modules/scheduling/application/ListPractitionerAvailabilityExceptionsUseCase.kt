package com.clinic.platform.modules.scheduling.application

import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.practitioner.domain.PractitionerRepository
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityException
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityExceptionRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListPractitionerAvailabilityExceptionsUseCase(
    private val facilityRepository: FacilityRepository,
    private val practitionerRepository: PractitionerRepository,
    private val practitionerAvailabilityExceptionRepository:
        PractitionerAvailabilityExceptionRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        practitionerId: UUID
    ): List<PractitionerAvailabilityException> {

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

        return practitionerAvailabilityExceptionRepository
            .findAllByOrganizationIdAndFacilityIdAndPractitionerId(
                organizationId,
                facilityId,
                practitionerId
            )
    }
}