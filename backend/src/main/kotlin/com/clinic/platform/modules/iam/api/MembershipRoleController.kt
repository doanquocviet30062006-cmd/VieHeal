package com.clinic.platform.modules.iam.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.modules.iam.api.dto.AssignMembershipRoleRequest
import com.clinic.platform.modules.iam.api.dto.IamRoleResponse
import com.clinic.platform.modules.iam.application.AssignMembershipRoleUseCase
import com.clinic.platform.modules.iam.application.ListMembershipRolesUseCase
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
@RequestMapping("/api/v1/memberships")
class MembershipRoleController(
    private val assignMembershipRoleUseCase:
        AssignMembershipRoleUseCase,
    private val listMembershipRolesUseCase:
        ListMembershipRolesUseCase,
    private val accessAuthorizationService:
        AccessAuthorizationService
) {

    @PostMapping("/{membershipId}/roles")
    fun assignRole(
        @PathVariable membershipId: UUID,
        @Valid
        @RequestBody
        request: AssignMembershipRoleRequest,
        @AuthenticationPrincipal
        jwt: Jwt
    ): ResponseEntity<IamRoleResponse> {

        accessAuthorizationService
            .requireMembershipPermission(
                jwt = jwt,
                membershipId = membershipId,
                permission = PermissionCodes.IAM_ROLE_MANAGE
            )

        val role =
            assignMembershipRoleUseCase.execute(
                membershipId = membershipId,
                roleCode = request.roleCode
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                IamRoleResponse.fromDomain(role)
            )
    }

    @GetMapping("/{membershipId}/roles")
    fun listRoles(
        @PathVariable membershipId: UUID,
        @AuthenticationPrincipal
        jwt: Jwt
    ): ResponseEntity<List<IamRoleResponse>> {

        accessAuthorizationService
            .requireMembershipPermission(
                jwt = jwt,
                membershipId = membershipId,
                permission = PermissionCodes.IAM_ROLE_MANAGE
            )

        val roles =
            listMembershipRolesUseCase.execute(
                membershipId
            )

        return ResponseEntity.ok(
            roles.map {
                IamRoleResponse.fromDomain(it)
            }
        )
    }
}