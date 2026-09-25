package com.clinic.platform.modules.organization.application

import com.clinic.platform.modules.organization.domain.Organization
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.shared.errors.ResourceAlreadyExistsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateOrganizationUseCase(
    private val organizationRepository: OrganizationRepository
) {

    @Transactional
    fun execute(
        code: String,
        name: String
    ): Organization {

        val normalizedCode =
            code.trim().uppercase()

        if (
            organizationRepository
                .existsByCode(normalizedCode)
        ) {
            throw ResourceAlreadyExistsException(
                "Organization code already exists: $normalizedCode"
            )
        }

        val organization =
            Organization.create(
                code = normalizedCode,
                name = name
            )

        return organizationRepository.save(
            organization
        )
    }
}