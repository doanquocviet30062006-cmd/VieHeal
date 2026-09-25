package com.clinic.platform.modules.organization.application

import com.clinic.platform.modules.organization.domain.Facility
import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.shared.errors.ResourceAlreadyExistsException
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CreateFacilityUseCase(
    private val organizationRepository: OrganizationRepository,
    private val facilityRepository: FacilityRepository
) {

    @Transactional
    fun execute(
        organizationId: UUID,
        code: String,
        name: String,
        addressLine: String?,
        ward: String?,
        district: String?,
        province: String?,
        countryCode: String?,
        phone: String?,
        email: String?
    ): Facility {

        organizationRepository.findById(organizationId)
            ?: throw ResourceNotFoundException(
                "Organization not found: $organizationId"
            )

        val normalizedCode = code.trim().uppercase()

        if (
            facilityRepository
                .existsByOrganizationIdAndCode(
                    organizationId,
                    normalizedCode
                )
        ) {
            throw ResourceAlreadyExistsException(
                "Facility code already exists in organization: $normalizedCode"
            )
        }

        val facility =
            Facility.create(
                organizationId = organizationId,
                code = normalizedCode,
                name = name,
                addressLine = addressLine,
                ward = ward,
                district = district,
                province = province,
                countryCode = countryCode,
                phone = phone,
                email = email
            )

        return facilityRepository.save(facility)
    }
}
