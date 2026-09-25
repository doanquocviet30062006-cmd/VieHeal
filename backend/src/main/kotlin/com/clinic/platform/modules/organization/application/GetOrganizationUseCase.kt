package com.clinic.platform.modules.organization.application

import com.clinic.platform.modules.organization.domain.Organization
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetOrganizationUseCase(
    private val organizationRepository: OrganizationRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        id: UUID
    ): Organization {

        return organizationRepository
            .findById(id)
            ?: throw ResourceNotFoundException(
                "Organization not found: $id"
            )
    }
}