package com.clinic.platform.modules.iam.api

import com.clinic.platform.infrastructure.security.CurrentAccessContextResolver
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
    ): Map<String, Any?> {

        val context =
            currentAccessContextResolver.resolve(jwt)

        return linkedMapOf(
            "sub" to jwt.subject,
            "preferredUsername" to
                jwt.getClaimAsString("preferred_username"),
            "email" to
                jwt.getClaimAsString("email"),

            "iamUserId" to context.user.id,
            "displayName" to context.user.displayName,
            "iamStatus" to context.user.status,

            "organizations" to
                context.organizations.map { access ->
                    linkedMapOf(
                        "membershipId" to
                            access.membershipId,
                        "organizationId" to
                            access.organizationId,
                        "roles" to access.roles,
                        "permissions" to
                            access.permissions,
                        "facilityIds" to
                            access.facilityIds
                    )
                },

            "issuer" to jwt.issuer.toString()
        )
    }
}