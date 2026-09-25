package com.clinic.platform.modules.scheduling.application

import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityException
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityExceptionRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetPractitionerAvailabilityExceptionUseCase(
    private val practitionerAvailabilityExceptionRepository:
        PractitionerAvailabilityExceptionRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        practitionerId: UUID,
        exceptionId: UUID
    ): PractitionerAvailabilityException {

        val exception =
            practitionerAvailabilityExceptionRepository
                .findById(exceptionId)
                ?: throw ResourceNotFoundException(
                    "Availability exception not found: $exceptionId"
                )

        if (
            exception.organizationId != organizationId ||
            exception.facilityId != facilityId ||
            exception.practitionerId != practitionerId
        ) {
            throw ResourceNotFoundException(
                "Availability exception not found: $exceptionId"
            )
        }

        return exception
    }
}