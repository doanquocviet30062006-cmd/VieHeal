package com.clinic.platform.modules.organization.application

import com.clinic.platform.modules.organization.domain.Facility
import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListFacilitiesUseCase(
    private val organizationRepository: OrganizationRepository,
    private val facilityRepository: FacilityRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID
    ): List<Facility> {

        organizationRepository.findById(organizationId)
            ?: throw ResourceNotFoundException(
                "Organization not found: $organizationId"
            )

        return facilityRepository
            .findAllByOrganizationId(organizationId)
    }
}