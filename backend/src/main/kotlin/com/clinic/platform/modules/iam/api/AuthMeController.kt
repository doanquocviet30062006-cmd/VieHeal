package com.clinic.platform.modules.iam.api

import com.clinic.platform.infrastructure.security.CurrentAccessContextResolver
import com.clinic.platform.modules.iam.api.dto.AuthMeFacilityResponse
import com.clinic.platform.modules.iam.api.dto.AuthMeOrganizationResponse
import com.clinic.platform.modules.iam.api.dto.AuthMeResponse
import com.clinic.platform.modules.iam.api.dto.AuthMeUserResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthMeController(
    private val currentAccessContextResolver:
        CurrentAccessContextResolver
) {

    @GetMapping("/me")
    fun me(
        @AuthenticationPrincipal jwt: Jwt
    ): AuthMeResponse {

        val context =
            currentAccessContextResolver.resolve(jwt)

        if (context.systemRoles.isEmpty() && context.organizations.isEmpty()) {
            throw AccessDeniedException(
                "Authenticated identity has no active application access"
            )
        }

        return AuthMeResponse(
            user = AuthMeUserResponse(
                id = context.user.id,
                externalSubject = requireNotNull(context.user.externalSubject),
                displayName = context.user.displayName
            ),
            systemRoles = context.systemRoles,
            systemPermissions = context.systemPermissions,
            organizations = context.organizations.map { access ->
                AuthMeOrganizationResponse(
                    membershipId = access.membershipId,
                    organizationId = access.organizationId,
                    roles = access.roles,
                    permissions = access.permissions,
                    facilities = access.facilityIds.sorted().map(::AuthMeFacilityResponse)
                )
            }
        )
    }
}
