package com.clinic.platform.modules.iam.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.modules.iam.api.dto.AddOrganizationMemberRequest
import com.clinic.platform.modules.iam.api.dto.OrganizationMembershipResponse
import com.clinic.platform.modules.iam.application.CreateOrganizationMembershipUseCase
import com.clinic.platform.modules.iam.application.ListOrganizationMembersUseCase
import com.clinic.platform.shared.security.PermissionCodes
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
class OrganizationMembershipController(
    private val createOrganizationMembershipUseCase:
        CreateOrganizationMembershipUseCase,
    private val listOrganizationMembersUseCase:
        ListOrganizationMembersUseCase,
    private val accessAuthorizationService:
        AccessAuthorizationService
) {

    @PostMapping("/{organizationId}/members")
    fun addMember(
        @PathVariable organizationId: UUID,
        @Valid
        @RequestBody
        request: AddOrganizationMemberRequest,
        @AuthenticationPrincipal
        jwt: Jwt
    ): ResponseEntity<OrganizationMembershipResponse> {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.IAM_USER_MANAGE
            )

        val membership =
            createOrganizationMembershipUseCase.execute(
                organizationId = organizationId,
                userId = requireNotNull(request.userId)
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                OrganizationMembershipResponse.fromDomain(
                    membership
                )
            )
    }

    @GetMapping("/{organizationId}/members")
    fun listMembers(
        @PathVariable organizationId: UUID,
        @AuthenticationPrincipal
        jwt: Jwt
    ): ResponseEntity<List<OrganizationMembershipResponse>> {

        accessAuthorizationService
            .requireOrganizationPermission(
                jwt = jwt,
                organizationId = organizationId,
                permission = PermissionCodes.IAM_USER_READ
            )

        val memberships =
            listOrganizationMembersUseCase.execute(
                organizationId
            )

        return ResponseEntity.ok(
            memberships.map {
                OrganizationMembershipResponse.fromDomain(it)
            }
        )
    }
}