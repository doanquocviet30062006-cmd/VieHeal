package com.clinic.platform.modules.organization.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.shared.security.PermissionCodes
import com.clinic.platform.modules.organization.api.dto.CreateOrganizationRequest
import com.clinic.platform.modules.organization.api.dto.OrganizationResponse
import com.clinic.platform.modules.organization.application.CreateOrganizationUseCase
import com.clinic.platform.modules.organization.application.GetOrganizationUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/organizations")
class OrganizationController(
    private val createOrganizationUseCase:
        CreateOrganizationUseCase,
    private val getOrganizationUseCase:
        GetOrganizationUseCase,
    private val accessAuthorizationService:
        AccessAuthorizationService
) {

    @PostMapping
    fun create(
        @Valid
        @RequestBody
        request: CreateOrganizationRequest
    ): ResponseEntity<OrganizationResponse> {

        val organization =
            createOrganizationUseCase.execute(
                code = request.code,
                name = request.name
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                OrganizationResponse.fromDomain(
                    organization
                )
            )
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<OrganizationResponse> {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = id,
                permission =
                    PermissionCodes.ORGANIZATION_READ
            )

        val organization =
            getOrganizationUseCase.execute(id)

        return ResponseEntity.ok(
            OrganizationResponse.fromDomain(
                organization
            )
        )
    }
}