package com.clinic.platform.modules.practitioner.application

import com.clinic.platform.modules.practitioner.domain.Practitioner
import com.clinic.platform.modules.practitioner.domain.PractitionerRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetPractitionerUseCase(
    private val practitionerRepository: PractitionerRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        practitionerId: UUID
    ): Practitioner {

        val practitioner =
            practitionerRepository.findById(practitionerId)
                ?: throw ResourceNotFoundException(
                    "Practitioner not found: $practitionerId"
                )

        if (practitioner.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Practitioner not found: $practitionerId"
            )
        }

        return practitioner
    }
}