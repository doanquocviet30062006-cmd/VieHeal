package com.clinic.platform.modules.reception.application

import com.clinic.platform.modules.reception.domain.QueueEntry
import com.clinic.platform.modules.reception.domain.QueueEntryRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetQueueEntryUseCase(
    private val queueEntryRepository: QueueEntryRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        queueEntryId: UUID
    ): QueueEntry {

        val queueEntry =
            queueEntryRepository.findById(queueEntryId)
                ?: throw ResourceNotFoundException(
                    "Queue entry not found in facility: $queueEntryId"
                )

        if (
            queueEntry.organizationId != organizationId ||
            queueEntry.facilityId != facilityId
        ) {
            throw ResourceNotFoundException(
                "Queue entry not found in facility: $queueEntryId"
            )
        }

        return queueEntry
    }
}