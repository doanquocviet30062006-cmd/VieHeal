package com.clinic.platform.modules.scheduling.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.infrastructure.security.CurrentIamUserResolver
import com.clinic.platform.modules.scheduling.api.dto.FacilitySchedulingSettingsResponse
import com.clinic.platform.modules.scheduling.api.dto.UpsertFacilitySchedulingSettingsRequest
import com.clinic.platform.modules.scheduling.application.CreateFacilitySchedulingSettingsUseCase
import com.clinic.platform.modules.scheduling.application.GetFacilitySchedulingSettingsUseCase
import com.clinic.platform.modules.scheduling.application.UpdateFacilitySchedulingSettingsUseCase
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
class FacilitySchedulingSettingsController(
    private val createFacilitySchedulingSettingsUseCase:
        CreateFacilitySchedulingSettingsUseCase,
    private val getFacilitySchedulingSettingsUseCase:
        GetFacilitySchedulingSettingsUseCase,
    private val updateFacilitySchedulingSettingsUseCase:
        UpdateFacilitySchedulingSettingsUseCase,
    private val accessAuthorizationService:
        AccessAuthorizationService,
    private val currentIamUserResolver:
        CurrentIamUserResolver
) {

    @PostMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/scheduling-settings"
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @Valid
        @RequestBody
        request: UpsertFacilitySchedulingSettingsRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): FacilitySchedulingSettingsResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SCHEDULE_CREATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val settings =
            createFacilitySchedulingSettingsUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                timeZoneId = request.timeZoneId,
                actorUserId = currentUser.id
            )

        return FacilitySchedulingSettingsResponse.from(
            settings
        )
    }

    @GetMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/scheduling-settings"
    )
    fun get(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): FacilitySchedulingSettingsResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SCHEDULE_READ
            )

        val settings =
            getFacilitySchedulingSettingsUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId
            )

        return FacilitySchedulingSettingsResponse.from(
            settings
        )
    }

    @PutMapping(
        "/organizations/{organizationId}/facilities/{facilityId}/scheduling-settings"
    )
    fun update(
        @PathVariable
        organizationId: UUID,

        @PathVariable
        facilityId: UUID,

        @Valid
        @RequestBody
        request: UpsertFacilitySchedulingSettingsRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): FacilitySchedulingSettingsResponse {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.SCHEDULE_UPDATE
            )

        val currentUser =
            currentIamUserResolver.resolve(jwt)

        val settings =
            updateFacilitySchedulingSettingsUseCase.execute(
                organizationId = organizationId,
                facilityId = facilityId,
                timeZoneId = request.timeZoneId,
                actorUserId = currentUser.id
            )

        return FacilitySchedulingSettingsResponse.from(
            settings
        )
    }
}