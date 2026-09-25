package com.clinic.platform.modules.organization.application

import com.clinic.platform.modules.organization.domain.Facility
import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetFacilityUseCase(
    private val facilityRepository: FacilityRepository
) {

    @Transactional(readOnly = true)
    fun execute(id: UUID): Facility =
        facilityRepository.findById(id)
            ?: throw ResourceNotFoundException(
                "Facility not found: $id"
            )
}