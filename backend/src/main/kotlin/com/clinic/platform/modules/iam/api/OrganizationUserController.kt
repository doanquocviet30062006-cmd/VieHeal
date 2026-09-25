package com.clinic.platform.modules.iam.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.modules.iam.api.dto.CreateOrganizationUserRequest
import com.clinic.platform.modules.iam.api.dto.IamUserResponse
import com.clinic.platform.modules.iam.application.ProvisionOrganizationUserUseCase
import com.clinic.platform.shared.security.PermissionCodes
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/organizations")
class OrganizationUserController(
    private val provisionOrganizationUserUseCase:
        ProvisionOrganizationUserUseCase,
    private val accessAuthorizationService:
        AccessAuthorizationService
) {

    @PostMapping("/{organizationId}/users")
    fun createUser(
        @PathVariable
        organizationId: UUID,

        @Valid
        @RequestBody
        request: CreateOrganizationUserRequest,

        @AuthenticationPrincipal
        jwt: Jwt
    ): ResponseEntity<IamUserResponse> {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.IAM_USER_MANAGE
            )

        val result =
            provisionOrganizationUserUseCase.execute(
                organizationId = organizationId,
                email = request.email,
                phone = request.phone,
                displayName = request.displayName
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                IamUserResponse.fromDomain(
                    result.user
                )
            )
    }
}