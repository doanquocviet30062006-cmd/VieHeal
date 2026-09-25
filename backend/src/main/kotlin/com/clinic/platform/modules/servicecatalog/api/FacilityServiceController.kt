package com.clinic.platform.modules.servicecatalog.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.infrastructure.security.CurrentIamUserResolver
import com.clinic.platform.modules.servicecatalog.api.dto.CreateFacilityServiceRequest
import com.clinic.platform.modules.servicecatalog.api.dto.FacilityServiceResponse
import com.clinic.platform.modules.servicecatalog.api.dto.UpdateFacilityServiceRequest
import com.clinic.platform.modules.servicecatalog.application.CreateFacilityServiceUseCase
import com.clinic.platform.modules.servicecatalog.application.GetFacilityServiceUseCase
import com.clinic.platform.modules.servicecatalog.application.ListFacilityServicesUseCase
import com.clinic.platform.modules.servicecatalog.application.UpdateFacilityServiceUseCase
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
class FacilityServiceController(
    private val createFacilityServiceUseCase: CreateFacilityServiceUseCase,
    private val getFacilityServiceUseCase: GetFacilityServiceUseCase,
    private val listFacilityServicesUseCase: ListFacilityServicesUseCase,
    private val updateFacilityServiceUseCase: UpdateFacilityServiceUseCase,
    private val accessAuthorizationService: AccessAuthorizationService,
    private val currentIamUserResolver: CurrentIamUserResolver
) {

    @PostMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/services"
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @Valid
        @RequestBody
        request: CreateFacilityServiceRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): FacilityServiceResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SERVICE_CREATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val facilityService =
            createFacilityServiceUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                serviceId = request.serviceId,
                durationMinutes =
                    request.durationMinutes,
                priceAmount =
                    request.priceAmount,
                currencyCode =
                    request.currencyCode,
                bookingEnabled =
                    request.bookingEnabled,
                actorUserId =
                    currentUser.id
            )

        return FacilityServiceResponse.from(
            facilityService
        )
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/services"
    )
    fun list(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): List<FacilityServiceResponse> {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SERVICE_READ
            )

        return listFacilityServicesUseCase
            .execute(
                organizationId = organizationId,
                facilityId = facilityId
            )
            .map(FacilityServiceResponse::from)
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/services/{facilityServiceId}"
    )
    fun get(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        facilityServiceId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): FacilityServiceResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SERVICE_READ
            )

        val facilityService =
            getFacilityServiceUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                facilityServiceId =
                    facilityServiceId
            )

        return FacilityServiceResponse.from(
            facilityService
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/services/{facilityServiceId}"
    )
    fun update(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @PathVariable
        facilityServiceId: UUID,

        @Valid
        @RequestBody
        request: UpdateFacilityServiceRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): FacilityServiceResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SERVICE_UPDATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val facilityService =
            updateFacilityServiceUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                facilityServiceId =
                    facilityServiceId,
                durationMinutes =
                    request.durationMinutes,
                priceAmount =
                    request.priceAmount,
                currencyCode =
                    request.currencyCode,
                bookingEnabled =
                    request.bookingEnabled,
                actorUserId =
                    currentUser.id
            )

        return FacilityServiceResponse.from(
            facilityService
        )
    }
}
