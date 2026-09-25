package com.clinic.platform.modules.servicecatalog.application

import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.servicecatalog.domain.MedicalService
import com.clinic.platform.modules.servicecatalog.domain.MedicalServiceRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListMedicalServicesUseCase(
    private val organizationRepository: OrganizationRepository,
    private val medicalServiceRepository: MedicalServiceRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID
    ): List<MedicalService> {

        organizationRepository.findById(organizationId)
            ?: throw ResourceNotFoundException(
                "Organization not found: $organizationId"
            )

        return medicalServiceRepository
            .findAllByOrganizationId(
                organizationId
            )
    }
}