package com.clinic.platform.modules.practitioner.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.infrastructure.security.CurrentIamUserResolver
import com.clinic.platform.modules.practitioner.api.dto.CreatePractitionerRequest
import com.clinic.platform.modules.practitioner.api.dto.PractitionerResponse
import com.clinic.platform.modules.practitioner.api.dto.UpdatePractitionerRequest
import com.clinic.platform.modules.practitioner.application.CreatePractitionerUseCase
import com.clinic.platform.modules.practitioner.application.GetPractitionerUseCase
import com.clinic.platform.modules.practitioner.application.ListPractitionersUseCase
import com.clinic.platform.modules.practitioner.application.UpdatePractitionerUseCase
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
class PractitionerController(
    private val createPractitionerUseCase: CreatePractitionerUseCase,
    private val getPractitionerUseCase: GetPractitionerUseCase,
    private val listPractitionersUseCase: ListPractitionersUseCase,
    private val updatePractitionerUseCase: UpdatePractitionerUseCase,
    private val accessAuthorizationService: AccessAuthorizationService,
    private val currentIamUserResolver: CurrentIamUserResolver
) {

    @PostMapping(
        "/organizations/{organizationId}/practitioners"
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable
        organizationId: UUID,

        @Valid
        @RequestBody
        request: CreatePractitionerRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): PractitionerResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.PRACTITIONER_CREATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val practitioner =
            createPractitionerUseCase.execute(
                organizationId = organizationId,
                membershipId = request.membershipId,
                practitionerCode = request.practitionerCode,
                fullName = request.fullName,
                practitionerType = request.practitionerType,
                licenseNumber = request.licenseNumber,
                specialty = request.specialty,
                phone = request.phone,
                email = request.email,
                actorUserId = currentUser.id
            )

        return PractitionerResponse.from(practitioner)
    }

    @GetMapping(
        "/organizations/{organizationId}/practitioners"
    )
    fun list(
        @PathVariable
        organizationId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): List<PractitionerResponse> {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.PRACTITIONER_READ
            )

        return listPractitionersUseCase
            .execute(
                organizationId = organizationId
            )
            .map(PractitionerResponse::from)
    }

    @GetMapping(
        "/organizations/{organizationId}/practitioners/{practitionerId}"
    )
    fun get(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        practitionerId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): PractitionerResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.PRACTITIONER_READ
            )

        val practitioner =
            getPractitionerUseCase.execute(
                organizationId = organizationId,
                practitionerId = practitionerId
            )

        return PractitionerResponse.from(practitioner)
    }

    @PutMapping(
        "/organizations/{organizationId}/practitioners/{practitionerId}"
    )
    fun update(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        practitionerId: UUID,

        @Valid
        @RequestBody
        request: UpdatePractitionerRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): PractitionerResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.PRACTITIONER_UPDATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val practitioner =
            updatePractitionerUseCase.execute(
                organizationId = organizationId,
                practitionerId = practitionerId,
                fullName = request.fullName,
                practitionerType = request.practitionerType,
                licenseNumber = request.licenseNumber,
                specialty = request.specialty,
                phone = request.phone,
                email = request.email,
                actorUserId = currentUser.id
            )

        return PractitionerResponse.from(practitioner)
    }
}