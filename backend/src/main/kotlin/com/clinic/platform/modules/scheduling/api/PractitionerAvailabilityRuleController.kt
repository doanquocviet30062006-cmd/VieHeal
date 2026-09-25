package com.clinic.platform.modules.scheduling.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.infrastructure.security.CurrentIamUserResolver
import com.clinic.platform.modules.scheduling.api.dto.CreatePractitionerAvailabilityRuleRequest
import com.clinic.platform.modules.scheduling.api.dto.PractitionerAvailabilityRuleResponse
import com.clinic.platform.modules.scheduling.api.dto.UpdatePractitionerAvailabilityRuleRequest
import com.clinic.platform.modules.scheduling.application.CreatePractitionerAvailabilityRuleUseCase
import com.clinic.platform.modules.scheduling.application.GetPractitionerAvailabilityRuleUseCase
import com.clinic.platform.modules.scheduling.application.ListPractitionerAvailabilityRulesUseCase
import com.clinic.platform.modules.scheduling.application.UpdatePractitionerAvailabilityRuleUseCase
import com.clinic.platform.shared.security.PermissionCodes
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class PractitionerAvailabilityRuleController(
    private val createPractitionerAvailabilityRuleUseCase:
        CreatePractitionerAvailabilityRuleUseCase,
    private val getPractitionerAvailabilityRuleUseCase:
        GetPractitionerAvailabilityRuleUseCase,
    private val listPractitionerAvailabilityRulesUseCase:
        ListPractitionerAvailabilityRulesUseCase,
    private val updatePractitionerAvailabilityRuleUseCase:
        UpdatePractitionerAvailabilityRuleUseCase,
    private val accessAuthorizationService:
        AccessAuthorizationService,
    private val currentIamUserResolver:
        CurrentIamUserResolver
) {

    @PostMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-rules"
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        practitionerId: UUID,

        @Valid
        @RequestBody
        request: CreatePractitionerAvailabilityRuleRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): PractitionerAvailabilityRuleResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SCHEDULE_CREATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val rule =
            createPractitionerAvailabilityRuleUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                practitionerId = practitionerId,
                dayOfWeek = request.dayOfWeek,
                startLocalTime = request.startLocalTime,
                endLocalTime = request.endLocalTime,
                effectiveFrom = request.effectiveFrom,
                effectiveTo = request.effectiveTo,
                actorUserId = currentUser.id
            )

        return PractitionerAvailabilityRuleResponse.from(
            rule
        )
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-rules"
    )
    fun list(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        practitionerId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): List<PractitionerAvailabilityRuleResponse> {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SCHEDULE_READ
            )

        return listPractitionerAvailabilityRulesUseCase
            .execute(
                organizationId = organizationId,
                facilityId = facilityId,
                practitionerId = practitionerId
            )
            .map(
                PractitionerAvailabilityRuleResponse::from
            )
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-rules/{ruleId}"
    )
    fun get(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        practitionerId: UUID,

        @PathVariable
        ruleId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): PractitionerAvailabilityRuleResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SCHEDULE_READ
            )

        val rule =
            getPractitionerAvailabilityRuleUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                practitionerId = practitionerId,
                ruleId = ruleId
            )

        return PractitionerAvailabilityRuleResponse.from(
            rule
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-rules/{ruleId}"
    )
    fun update(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        practitionerId: UUID,

        @PathVariable
        ruleId: UUID,

        @Valid
        @RequestBody
        request: UpdatePractitionerAvailabilityRuleRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): PractitionerAvailabilityRuleResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SCHEDULE_UPDATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val rule =
            updatePractitionerAvailabilityRuleUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                practitionerId = practitionerId,
                ruleId = ruleId,
                dayOfWeek = request.dayOfWeek,
                startLocalTime = request.startLocalTime,
                endLocalTime = request.endLocalTime,
                effectiveFrom = request.effectiveFrom,
                effectiveTo = request.effectiveTo,
                actorUserId = currentUser.id
            )

        return PractitionerAvailabilityRuleResponse.from(
            rule
        )
    }
}