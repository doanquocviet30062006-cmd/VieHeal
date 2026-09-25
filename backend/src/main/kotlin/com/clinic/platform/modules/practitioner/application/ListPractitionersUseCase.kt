package com.clinic.platform.modules.practitioner.application

import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.practitioner.domain.Practitioner
import com.clinic.platform.modules.practitioner.domain.PractitionerRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListPractitionersUseCase(
    private val practitionerRepository: PractitionerRepository,
    private val organizationRepository: OrganizationRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID
    ): List<Practitioner> {

        organizationRepository.findById(organizationId)
            ?: throw ResourceNotFoundException(
                "Organization not found: $organizationId"
            )

        return practitionerRepository
            .findAllByOrganizationId(
                organizationId
            )
    }
}