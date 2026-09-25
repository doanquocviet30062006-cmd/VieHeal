package com.clinic.platform.modules.servicecatalog.application

import com.clinic.platform.modules.servicecatalog.domain.MedicalService
import com.clinic.platform.modules.servicecatalog.domain.MedicalServiceRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetMedicalServiceUseCase(
    private val medicalServiceRepository: MedicalServiceRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        serviceId: UUID
    ): MedicalService {

        val medicalService =
            medicalServiceRepository.findById(serviceId)
                ?: throw ResourceNotFoundException(
                    "Service not found: $serviceId"
                )

        if (medicalService.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Service not found: $serviceId"
            )
        }

        return medicalService
    }
}