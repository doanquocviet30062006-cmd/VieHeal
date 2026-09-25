package com.clinic.platform.modules.iam.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.modules.iam.api.dto.AssignFacilityRequest
import com.clinic.platform.modules.iam.api.dto.FacilityAssignmentResponse
import com.clinic.platform.modules.iam.application.AssignFacilityUseCase
import com.clinic.platform.modules.iam.application.ListMembershipFacilitiesUseCase
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
class MembershipFacilityController(
    private val assignFacilityUseCase:
        AssignFacilityUseCase,
    private val listMembershipFacilitiesUseCase:
        ListMembershipFacilitiesUseCase,
    private val accessAuthorizationService:
        AccessAuthorizationService
) {

    @PostMapping("/{membershipId}/facilities")
    fun assignFacility(
        @PathVariable membershipId: UUID,
        @Valid
        @RequestBody
        request: AssignFacilityRequest,
        @AuthenticationPrincipal
        jwt: Jwt
    ): ResponseEntity<FacilityAssignmentResponse> {

        accessAuthorizationService
            .requireMembershipPermission(
                jwt = jwt,
                membershipId = membershipId,
                permission = PermissionCodes.IAM_USER_MANAGE
            )

        val assignment =
            assignFacilityUseCase.execute(
                membershipId = membershipId,
                facilityId = requireNotNull(
                    request.facilityId
                )
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                FacilityAssignmentResponse.fromDomain(
                    assignment
                )
            )
    }

    @GetMapping("/{membershipId}/facilities")
    fun listFacilities(
        @PathVariable membershipId: UUID,
        @AuthenticationPrincipal
        jwt: Jwt
    ): ResponseEntity<List<FacilityAssignmentResponse>> {

        accessAuthorizationService
            .requireMembershipPermission(
                jwt = jwt,
                membershipId = membershipId,
                permission = PermissionCodes.IAM_USER_READ
            )

        val assignments =
            listMembershipFacilitiesUseCase.execute(
                membershipId
            )

        return ResponseEntity.ok(
            assignments.map {
                FacilityAssignmentResponse.fromDomain(it)
            }
        )
    }
}