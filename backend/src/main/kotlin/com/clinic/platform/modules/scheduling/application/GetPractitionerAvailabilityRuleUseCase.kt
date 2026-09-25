package com.clinic.platform.modules.scheduling.application

import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityRule
import com.clinic.platform.modules.scheduling.domain.PractitionerAvailabilityRuleRepository
import com.clinic.platform.shared.errors.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GetPractitionerAvailabilityRuleUseCase(
    private val practitionerAvailabilityRuleRepository:
        PractitionerAvailabilityRuleRepository
) {

    @Transactional(readOnly = true)
    fun execute(
        organizationId: UUID,
        facilityId: UUID,
        practitionerId: UUID,
        ruleId: UUID
    ): PractitionerAvailabilityRule {

        val rule =
            practitionerAvailabilityRuleRepository.findById(ruleId)
                ?: throw ResourceNotFoundException(
                    "Availability rule not found: $ruleId"
                )

        if (
            rule.organizationId != organizationId ||
            rule.facilityId != facilityId ||
            rule.practitionerId != practitionerId
        ) {
            throw ResourceNotFoundException(
                "Availability rule not found: $ruleId"
            )
        }

        return rule
    }
}