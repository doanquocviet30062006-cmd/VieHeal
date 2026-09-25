package com.clinic.platform.modules.organization.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.shared.security.PermissionCodes
import com.clinic.platform.modules.organization.api.dto.CreateFacilityRequest
import com.clinic.platform.modules.organization.api.dto.FacilityResponse
import com.clinic.platform.modules.organization.application.CreateFacilityUseCase
import com.clinic.platform.modules.organization.application.GetFacilityUseCase
import com.clinic.platform.modules.organization.application.ListFacilitiesUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class FacilityController(
    private val createFacilityUseCase: CreateFacilityUseCase,
    private val getFacilityUseCase: GetFacilityUseCase,
    private val listFacilitiesUseCase: ListFacilitiesUseCase,
    private val accessAuthorizationService: AccessAuthorizationService
) {

    @PostMapping("/organizations/{organizationId}/facilities")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable
        organizationId: UUID,

        @Valid
        @RequestBody
        request: CreateFacilityRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): FacilityResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.FACILITY_MANAGE
            )

        val facility =
            createFacilityUseCase.execute(
                organizationId = organizationId,
                code = request.code,
                name = request.name,
                addressLine = request.addressLine,
                ward = request.ward,
                district = request.district,
                province = request.province,
                countryCode = request.countryCode,
                phone = request.phone,
                email = request.email
            )

        return FacilityResponse.from(facility)
    }

    @GetMapping("/organizations/{organizationId}/facilities")
    fun list(
        @PathVariable
        organizationId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): List<FacilityResponse> {

        val access =
            accessAuthorizationService
                .requireOrganizationPermission(
                    jwt = jwt,
                    organizationId = organizationId,
                    permission = PermissionCodes.FACILITY_READ
                )

        return listFacilitiesUseCase
            .execute(organizationId)
            .filter {
                it.id in access.facilityIds
            }
            .map(FacilityResponse::from)
    }

    @GetMapping("/facilities/{facilityId}")
    fun get(
        @PathVariable
        facilityId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): FacilityResponse {

        accessAuthorizationService
            .requireFacilityPermission(
                jwt = jwt,
                facilityId = facilityId,
                permission = PermissionCodes.FACILITY_READ
            )

        return FacilityResponse.from(
            getFacilityUseCase.execute(facilityId)
        )
    }
}