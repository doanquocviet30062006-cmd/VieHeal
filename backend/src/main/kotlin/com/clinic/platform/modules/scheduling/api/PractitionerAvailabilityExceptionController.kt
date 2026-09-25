package com.clinic.platform.modules.scheduling.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.infrastructure.security.CurrentIamUserResolver
import com.clinic.platform.modules.scheduling.api.dto.CreatePractitionerAvailabilityExceptionRequest
import com.clinic.platform.modules.scheduling.api.dto.PractitionerAvailabilityExceptionResponse
import com.clinic.platform.modules.scheduling.api.dto.UpdatePractitionerAvailabilityExceptionRequest
import com.clinic.platform.modules.scheduling.application.CreatePractitionerAvailabilityExceptionUseCase
import com.clinic.platform.modules.scheduling.application.GetPractitionerAvailabilityExceptionUseCase
import com.clinic.platform.modules.scheduling.application.ListPractitionerAvailabilityExceptionsUseCase
import com.clinic.platform.modules.scheduling.application.UpdatePractitionerAvailabilityExceptionUseCase
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
class PractitionerAvailabilityExceptionController(
    private val createPractitionerAvailabilityExceptionUseCase:
        CreatePractitionerAvailabilityExceptionUseCase,
    private val getPractitionerAvailabilityExceptionUseCase:
        GetPractitionerAvailabilityExceptionUseCase,
    private val listPractitionerAvailabilityExceptionsUseCase:
        ListPractitionerAvailabilityExceptionsUseCase,
    private val updatePractitionerAvailabilityExceptionUseCase:
        UpdatePractitionerAvailabilityExceptionUseCase,
    private val accessAuthorizationService:
        AccessAuthorizationService,
    private val currentIamUserResolver:
        CurrentIamUserResolver
) {

    @PostMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-exceptions"
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
        request: CreatePractitionerAvailabilityExceptionRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): PractitionerAvailabilityExceptionResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SCHEDULE_CREATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val exception =
            createPractitionerAvailabilityExceptionUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                practitionerId = practitionerId,
                exceptionType = request.exceptionType,
                startAt = request.startAt,
                endAt = request.endAt,
                reason = request.reason,
                actorUserId = currentUser.id
            )

        return PractitionerAvailabilityExceptionResponse.from(
            exception
        )
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-exceptions"
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
    ): List<PractitionerAvailabilityExceptionResponse> {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SCHEDULE_READ
            )

        return listPractitionerAvailabilityExceptionsUseCase
            .execute(
                organizationId = organizationId,
                facilityId = facilityId,
                practitionerId = practitionerId
            )
            .map(
                PractitionerAvailabilityExceptionResponse::from
            )
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-exceptions/{exceptionId}"
    )
    fun get(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        practitionerId: UUID,

        @PathVariable
        exceptionId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): PractitionerAvailabilityExceptionResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SCHEDULE_READ
            )

        val exception =
            getPractitionerAvailabilityExceptionUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                practitionerId = practitionerId,
                exceptionId = exceptionId
            )

        return PractitionerAvailabilityExceptionResponse.from(
            exception
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-exceptions/{exceptionId}"
    )
    fun update(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        practitionerId: UUID,

        @PathVariable
        exceptionId: UUID,

        @Valid
        @RequestBody
        request: UpdatePractitionerAvailabilityExceptionRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): PractitionerAvailabilityExceptionResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SCHEDULE_UPDATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val exception =
            updatePractitionerAvailabilityExceptionUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                practitionerId = practitionerId,
                exceptionId = exceptionId,
                exceptionType = request.exceptionType,
                startAt = request.startAt,
                endAt = request.endAt,
                reason = request.reason,
                actorUserId = currentUser.id
            )

        return PractitionerAvailabilityExceptionResponse.from(
            exception
        )
    }
}