package com.clinic.platform.modules.servicecatalog.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.infrastructure.security.CurrentIamUserResolver
import com.clinic.platform.modules.servicecatalog.api.dto.CreateMedicalServiceRequest
import com.clinic.platform.modules.servicecatalog.api.dto.MedicalServiceResponse
import com.clinic.platform.modules.servicecatalog.api.dto.UpdateMedicalServiceRequest
import com.clinic.platform.modules.servicecatalog.application.CreateMedicalServiceUseCase
import com.clinic.platform.modules.servicecatalog.application.GetMedicalServiceUseCase
import com.clinic.platform.modules.servicecatalog.application.ListMedicalServicesUseCase
import com.clinic.platform.modules.servicecatalog.application.UpdateMedicalServiceUseCase
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
class MedicalServiceController(
    private val createMedicalServiceUseCase: CreateMedicalServiceUseCase,
    private val getMedicalServiceUseCase: GetMedicalServiceUseCase,
    private val listMedicalServicesUseCase: ListMedicalServicesUseCase,
    private val updateMedicalServiceUseCase: UpdateMedicalServiceUseCase,
    private val accessAuthorizationService: AccessAuthorizationService,
    private val currentIamUserResolver: CurrentIamUserResolver
) {

    @PostMapping(
        "/organizations/{organizationId}/services"
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable
        organizationId: UUID,

        @Valid
        @RequestBody
        request: CreateMedicalServiceRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): MedicalServiceResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SERVICE_CREATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val medicalService =
            createMedicalServiceUseCase.execute(
                organizationId = organizationId,
                serviceCode = request.serviceCode,
                name = request.name,
                description = request.description,
                category = request.category,
                defaultDurationMinutes =
                    request.defaultDurationMinutes,
                actorUserId = currentUser.id
            )

        return MedicalServiceResponse.from(
            medicalService
        )
    }

    @GetMapping(
        "/organizations/{organizationId}/services"
    )
    fun list(
        @PathVariable
        organizationId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): List<MedicalServiceResponse> {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SERVICE_READ
            )

        return listMedicalServicesUseCase
            .execute(
                organizationId = organizationId
            )
            .map(MedicalServiceResponse::from)
    }

    @GetMapping(
        "/organizations/{organizationId}/services/{serviceId}"
    )
    fun get(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        serviceId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): MedicalServiceResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SERVICE_READ
            )

        val medicalService =
            getMedicalServiceUseCase.execute(
                organizationId = organizationId,
                serviceId = serviceId
            )

        return MedicalServiceResponse.from(
            medicalService
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/services/{serviceId}"
    )
    fun update(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        serviceId: UUID,

        @Valid
        @RequestBody
        request: UpdateMedicalServiceRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): MedicalServiceResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SERVICE_UPDATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val medicalService =
            updateMedicalServiceUseCase.execute(
                organizationId = organizationId,
                serviceId = serviceId,
                name = request.name,
                description = request.description,
                category = request.category,
                defaultDurationMinutes =
                    request.defaultDurationMinutes,
                actorUserId = currentUser.id
            )

        return MedicalServiceResponse.from(
            medicalService
        )
    }
}