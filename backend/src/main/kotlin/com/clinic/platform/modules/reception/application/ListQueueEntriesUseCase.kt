package com.clinic.platform.modules.reception.application

import com.clinic.platform.modules.organization.domain.FacilityRepository
import com.clinic.platform.modules.organization.domain.OrganizationRepository
import com.clinic.platform.modules.reception.domain.QueueEntry
import com.clinic.platform.modules.reception.domain.QueueEntryRepository
import com.clinic.platform.modules.reception.domain.QueueEntryStatus
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ListQueueEntriesUseCase(
    private val organizationRepository: OrganizationRepository,
    private val facilityRepository: FacilityRepository,
    private val queueEntryRepository: QueueEntryRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        status: QueueEntryStatus?
    ): List<QueueEntry> {

        organizationRepository.findById(organizationId)
            ?: throw ResourceNotFoundException(
                "Organization not found: $organizationId"
            )

        val facility =
            facilityRepository.findById(facilityId)
                ?: throw ResourceNotFoundException(
                    "Facility not found in organization: $facilityId"
                )

        if (facility.organizationId != organizationId) {
            throw ResourceNotFoundException(
                "Facility not found in organization: $facilityId"
            )
        }

        return if (status == null) {
            queueEntryRepository
                .findAllByOrganizationIdAndFacilityIdOrdered(
                    organizationId = organizationId,
                    facilityId = facilityId
                )
        } else {
            queueEntryRepository
                .findAllByOrganizationIdAndFacilityIdAndStatusOrdered(
                    organizationId = organizationId,
                    facilityId = facilityId,
                    status = status
                )
        }
    }
}